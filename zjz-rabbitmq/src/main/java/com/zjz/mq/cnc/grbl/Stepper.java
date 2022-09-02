package com.zjz.mq.cnc.grbl;

import com.zjz.mq.cnc.config.ThreadPoolConfig;
import com.zjz.mq.cnc.constant.SystemConstant;
import com.zjz.mq.cnc.obj.BlockT;
import com.zjz.mq.cnc.obj.StepperT;
import com.zjz.mq.cnc.obj.SystemT;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zjz
 * @date 2022/8/31 17:08
 */
@Slf4j
public class Stepper {

    private static final ThreadPoolExecutor threadPoolExecutor = ThreadPoolConfig.commonThreadPool();


    private static final int stepPortInvertMask = 28;

    private static BlockT currentBlock = new BlockT();
    private static final StepperT st = new StepperT();
    private static final SystemT sys = new SystemT();

    private static int TIM3_timer = 0;
    private static int TIM4_timer = 0;

    private static int stepPulseTime; // Step pulse reset time after step rise
    private static int dir_out_bits;        // The next stepping-bits to be output
    private static int step_out_bits;
    private static volatile boolean busy;
    private static final int CYCLES_PER_ACCELERATION_TICK = (72*1000000)/50;

    public static void TIM3_IRQHandler() {


        log.info("TIM3 && DIRECTION_GPIO---X_DIRECTION_GPIO_PIN-----{}", bitIsTrue(dir_out_bits, bit(5)));
        log.info("TIM3 && DIRECTION_GPIO---Y_DIRECTION_GPIO_PIN-----{}", bitIsTrue(dir_out_bits, bit(6)));
        log.info("TIM3 && DIRECTION_GPIO---Z_DIRECTION_GPIO_PIN-----{}", bitIsTrue(dir_out_bits, bit(7)));

        log.info("TIM3 && STEP_GPIO---X_STEP_GPIO_PIN-----{}", bitIsTrue(step_out_bits, bit(2)));
        log.info("TIM3 && STEP_GPIO---Y_STEP_GPIO_PIN-----{}", bitIsTrue(step_out_bits, bit(3)));
        log.info("TIM3 && STEP_GPIO---Z_STEP_GPIO_PIN-----{}", bitIsTrue(step_out_bits, bit(4)));

        // todo 重新配置线程4的时间
        TIM4_timer = 0;
        threadPoolExecutor.execute(Stepper::TIM4_IRQHandler);
        //清除TIM3更新中断标志
        TIM3_timer = 0;

        // If there is no current block, attempt to pop one from the buffer
        if (currentBlock == null) {
            // Anything in the buffer? If so, initialize next motion.
            currentBlock = Planner.planGetCurrentBlock();
            if (currentBlock != null) {
                st.setMinSafeRate(currentBlock.getRateDelta() + (currentBlock.getRateDelta() >> 1)); // 1.5 x rate_delta
                st.setCounterX(-(currentBlock.getStepEventCount() >> 1));
                st.setCounterY(st.getCounterX());
                st.setCounterZ(st.getCounterX());
                st.setEventCount(currentBlock.getStepEventCount());
                st.setStepEventsCompleted(0);
            }
        }

        if (currentBlock != null) {
            // Execute step displacement profile by bresenham line algorithm
            dir_out_bits = currentBlock.getDirectionBits();
            st.setCounterX(st.getCounterX() + currentBlock.getStepsX());
            if (st.getCounterX() > 0) {
                step_out_bits |= (1 << SystemConstant.X_STEP_BIT);
                st.setCounterX(st.getCounterX() - st.getEventCount());
                // 系统位置记录
//                if ((out_bits & ( 1 << SystemConstant.X_DIRECTION_BIT )) > 0) { sys.getPosition()[SystemConstant.X_AXIS]--; }
//                else { sys.getPosition()[SystemConstant.X_AXIS]++; }
            }

            st.setCounterY(st.getCounterY() + currentBlock.getStepsY());
            if (st.getCounterY() > 0) {
                step_out_bits |= (1 << SystemConstant.Y_STEP_BIT);
                st.setCounterY(st.getCounterY() - st.getEventCount());
//                if ((out_bits & (1 << SystemConstant.Y_DIRECTION_BIT)) > 0) { sys.getPosition()[SystemConstant.Y_AXIS]--; }
//                else { sys.getPosition()[SystemConstant.Y_AXIS]++; }
            }
            st.setCounterZ(st.getCounterZ() + currentBlock.getStepsZ());
            if (st.getCounterZ() > 0) {
                step_out_bits |= (1 << SystemConstant.Z_STEP_BIT);
                st.setCounterZ(st.getCounterZ() - st.getEventCount());
//                if ((out_bits & (1 << SystemConstant.Z_DIRECTION_BIT)) > 0) { sys.getPosition()[SystemConstant.Z_AXIS]--; }
//                else { sys.getPosition()[SystemConstant.Z_AXIS]++; }
            }

            st.setStepEventsCompleted(st.getStepEventsCompleted() + 1); // Iterate step events

            // While in block steps, check for de/ac-celeration events and execute them accordingly.
            if (st.getStepEventsCompleted() < currentBlock.getStepEventCount()) {
                if (sys.getState() == SystemConstant.STATE_HOLD) {

                    if ( iterateTrapezoidCycleCounter() ) {
                        // If deceleration complete, set system flags and shutdown steppers.
                        if (st.getTrapezoidAdjustedRate() <= currentBlock.getRateDelta()) {
//                            st_go_idle();
//                            bit_true(sys.execute,EXEC_CYCLE_STOP); // Flag main program that feed hold is complete.
                        } else {
                            st.setTrapezoidAdjustedRate(st.getTrapezoidAdjustedRate() - currentBlock.getRateDelta());
                            setStepEventsPerMinute(st.getTrapezoidAdjustedRate());
                        }
                    }

                } else {
                    if (st.getStepEventsCompleted() < currentBlock.getAccelerateUntil()) {
                        // Iterate cycle counter and check if speeds need to be increased.
                        if ( iterateTrapezoidCycleCounter() ) {
                            st.setTrapezoidAdjustedRate(st.getTrapezoidAdjustedRate() + currentBlock.getRateDelta());
                            if (st.getTrapezoidAdjustedRate() >= currentBlock.getNominalRate()) {
                                // Reached nominal rate a little early. Cruise at nominal rate until decelerate_after.
                                st.setTrapezoidAdjustedRate(currentBlock.getNominalRate());
                            }
                            setStepEventsPerMinute(st.getTrapezoidAdjustedRate());
                        }
                    } else if (st.getStepEventsCompleted() >= currentBlock.getDecelerateAfter()) {
                        if (st.getStepEventsCompleted() == currentBlock.getDecelerateAfter()) {
                            if (st.getTrapezoidAdjustedRate() == currentBlock.getNominalRate()) {
                                st.setTrapezoidTickCycleCounter(CYCLES_PER_ACCELERATION_TICK/2); // Trapezoid profile
                            } else {
                                st.setTrapezoidTickCycleCounter(CYCLES_PER_ACCELERATION_TICK-st.getTrapezoidTickCycleCounter()); // Triangle profile
                            }
                        } else {
                            // Iterate cycle counter and check if speeds need to be reduced.
                            if ( iterateTrapezoidCycleCounter() ) {
                                if (st.getTrapezoidAdjustedRate() > st.getMinSafeRate()) {
                                    st.setTrapezoidAdjustedRate(st.getTrapezoidAdjustedRate() - currentBlock.getRateDelta());
                                } else {
                                    st.setTrapezoidAdjustedRate(st.getTrapezoidAdjustedRate() >> 1); // Bit shift divide by 2
                                }
                                if (st.getTrapezoidAdjustedRate() < currentBlock.getFinalRate()) {
                                    // Reached final rate a little early. Cruise to end of block at final rate.
                                    st.setTrapezoidAdjustedRate(currentBlock.getFinalRate());
                                }
                                setStepEventsPerMinute(st.getTrapezoidAdjustedRate());
                            }
                        }
                    } else {
                        // No accelerations. Make sure we cruise exactly at the nominal rate.
                        if (st.getTrapezoidAdjustedRate() != currentBlock.getNominalRate()) {
                            st.setTrapezoidAdjustedRate(currentBlock.getNominalRate());
                            setStepEventsPerMinute(st.getTrapezoidAdjustedRate());
                        }
                    }
                }
            } else {
                // If current block is finished, reset pointer
                currentBlock = null;
                Planner.planDiscardCurrentBlock();
            }
        }
        int invert_mask = (1 << 6) | (1 << 7);
        step_out_bits ^= invert_mask;  // Apply step and direction invert mask
        busy = false;
    }

    public static void TIM4_IRQHandler() {
        try {
            Thread.sleep(TIM4_timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("TIM4 && STEP_GPIO-B-----X_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(2)));
        log.info("TIM4 && STEP_GPIO-B-----Y_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(3)));
        log.info("TIM4 && STEP_GPIO-B-----Z_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(4)));
    }

    public static boolean bitIsTrue(int x, int mask) {
        return (x & mask) != 0;
    }

    public static int bit(int n) {
        return 1 << n;
    }


    public static boolean iterateTrapezoidCycleCounter() {

        st.setTrapezoidTickCycleCounter((int) (st.getTrapezoidTickCycleCounter() + st.getCyclesPerStepEvent()));
        if(st.getTrapezoidTickCycleCounter() > CYCLES_PER_ACCELERATION_TICK) {
            st.setTrapezoidTickCycleCounter(st.getTrapezoidTickCycleCounter() - CYCLES_PER_ACCELERATION_TICK);
            return(true);
        } else {
            return(false);
        }
    }

    static void setStepEventsPerMinute(int steps_per_minute) {
        if (steps_per_minute < 800) { steps_per_minute = 800; }
        st.setCyclesPerStepEvent(configStepTimer((72/1000000*1000000*60)/steps_per_minute));
    }

    public static long configStepTimer(int cycles)
    {
        int ceiling;
        int prescaler;
        long actual_cycles;
        if (cycles <= 0xffffL) {
            ceiling = cycles;
            prescaler = 1; // prescaler: 0
            actual_cycles = ceiling;
        } else if (cycles <= 0x7ffffL) {
            ceiling = cycles >> 3;
            prescaler = 2; // prescaler: 8
            actual_cycles = ceiling * 8L;
        } else if (cycles <= 0x3fffffL) {
            ceiling =  cycles >> 6;
            prescaler = 3; // prescaler: 64
            actual_cycles = ceiling * 64L;
        } else if (cycles <= 0xffffffL) {
            ceiling =  (cycles >> 8);
            prescaler = 4; // prescaler: 256
            actual_cycles = ceiling * 256L;
        } else if (cycles <= 0x3ffffffL) {
            ceiling = (cycles >> 10);
            prescaler = 5; // prescaler: 1024
            actual_cycles = ceiling * 1024L;
        } else {
            // Okay, that was slower than we actually go. Just set the slowest speed
            ceiling = 0xffff;
            prescaler = 5;
            actual_cycles = 0xffff * 1024;
        }
        // Set prescaler
        TIM3_timer = (TIM3_timer & ~(0x07<<1)) | (prescaler<<1);
        // Set ceiling
//        OCR1A = ceiling;
        return actual_cycles;
    }


    public static void main(String[] args) {

//        threadPoolExecutor.execute(Stepper::TIM4_IRQHandler);

    }
}
