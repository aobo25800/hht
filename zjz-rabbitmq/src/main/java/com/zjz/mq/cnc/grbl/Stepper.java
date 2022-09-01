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


    private static int stepPortInvertMask;

    private static BlockT currentBlock;
    private static StepperT st;
    private static SystemT sys;

    private static int TIM3_timer;
    private static int TIM4_timer;

    private static int stepPulseTime; // Step pulse reset time after step rise
    private static int out_bits;        // The next stepping-bits to be output
    private static volatile boolean busy;
    private static final int CYCLES_PER_ACCELERATION_TICK = (72*1000000)/50;

    public static void TIM3_IRQHandler() {
        //清除TIM3更新中断标志
        TIM3_timer = 0;

        // Set the direction pins a couple of nanoseconds before we step the steppers
        // 在设置步进脉冲引脚前几个纳秒前设置方向引脚
        log.info("DIRECTION_GPIO---X_DIRECTION_GPIO_PIN-----{}",bitIsTrue(currentBlock.getDirectionBits(),bit(5)));
        log.info("DIRECTION_GPIO---Y_DIRECTION_GPIO_PIN-----{}",bitIsTrue(currentBlock.getDirectionBits(),bit(6)));
        log.info("DIRECTION_GPIO---Z_DIRECTION_GPIO_PIN-----{}",bitIsTrue(currentBlock.getDirectionBits(),bit(7)));

        log.info("STEP_GPIO---X_STEP_GPIO_PIN-----{}",bitIsTrue(currentBlock.getStepsX(), bit(2)));
        log.info("STEP_GPIO---Y_STEP_GPIO_PIN-----{}",bitIsTrue(currentBlock.getStepsY(), bit(3)));
        log.info("STEP_GPIO---Z_STEP_GPIO_PIN-----{}",bitIsTrue(currentBlock.getStepsZ(), bit(4)));

        // todo 重新配置线程4的时间
        TIM4_timer = 0;
        threadPoolExecutor.execute(() -> TIM4_IRQHandler());

        // If there is no current block, attempt to pop one from the buffer
        if (currentBlock == null) {
            // Anything in the buffer? If so, initialize next motion.
            // todo 获取block对象
//            currentBlock = plan_get_current_block();
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
            out_bits = currentBlock.getDirectionBits();
            st.setCounterX(st.getCounterX() + currentBlock.getStepsX());
            if (st.getCounterX() > 0) {
                out_bits |= (1 << SystemConstant.X_STEP_BIT);
                st.setCounterX(st.getCounterX() - st.getEventCount());
                if ((out_bits & ( 1 << SystemConstant.X_DIRECTION_BIT )) > 0) { sys.getPosition()[SystemConstant.X_AXIS]--; }
                else { sys.getPosition()[SystemConstant.X_AXIS]++; }
            }

            st.setCounterY(st.getCounterY() + currentBlock.getStepsY());
            if (st.getCounterY() > 0) {
                out_bits |= (1 << SystemConstant.Y_STEP_BIT);
                st.setCounterY(st.getCounterY() - st.getEventCount());
                if ((out_bits & (1 << SystemConstant.Y_DIRECTION_BIT)) > 0) { sys.getPosition()[SystemConstant.Y_AXIS]--; }
                else { sys.getPosition()[SystemConstant.Y_AXIS]++; }
            }
            st.setCounterZ(st.getCounterZ() + currentBlock.getStepsZ());
            if (st.getCounterZ() > 0) {
                out_bits |= (1 << SystemConstant.Z_STEP_BIT);
                st.setCounterZ(st.getCounterZ() - st.getEventCount());
                if ((out_bits & (1 << SystemConstant.Z_DIRECTION_BIT)) > 0) { sys.getPosition()[SystemConstant.Z_AXIS]--; }
                else { sys.getPosition()[SystemConstant.Z_AXIS]++; }
            }

            st.setStepEventsCompleted(st.getStepEventsCompleted() + 1); // Iterate step events

            // While in block steps, check for de/ac-celeration events and execute them accordingly.
            if (st.getStepEventsCompleted() < currentBlock.getStepEventCount()) {
                if (sys.getState() == SystemConstant.STATE_HOLD) {
                    // Check for and execute feed hold by enforcing a steady deceleration from the moment of
                    // execution. The rate of deceleration is limited by rate_delta and will never decelerate
                    // faster or slower than in normal operation. If the distance required for the feed hold
                    // deceleration spans more than one block, the initial rate of the following blocks are not
                    // updated and deceleration is continued according to their corresponding rate_delta.
                    // NOTE: The trapezoid tick cycle counter is not updated intentionally. This ensures that
                    // the deceleration is smooth regardless of where the feed hold is initiated and if the
                    // deceleration distance spans multiple blocks.
                    if ( iterateTrapezoidCycleCounter() ) {
                        // If deceleration complete, set system flags and shutdown steppers.
                        if (st.getTrapezoidAdjustedRate() <= currentBlock.getRateDelta()) {
                            // Just go idle. Do not NULL current block. The bresenham algorithm variables must
                            // remain intact to ensure the stepper path is exactly the same. Feed hold is still
                            // active and is released after the buffer has been reinitialized.
//                            st_go_idle();
//                            bit_true(sys.execute,EXEC_CYCLE_STOP); // Flag main program that feed hold is complete.
                        } else {
                            st.setTrapezoidAdjustedRate(st.getTrapezoidAdjustedRate() - currentBlock.getRateDelta());
                            setStepEventsPerMinute(st.getTrapezoidAdjustedRate());
                        }
                    }

                } else {
                    // The trapezoid generator always checks step event location to ensure de/ac-celerations are
                    // executed and terminated at exactly the right time. This helps prevent over/under-shooting
                    // the target position and speed.
                    // NOTE: By increasing the ACCELERATION_TICKS_PER_SECOND in config.h, the resolution of the
                    // discrete velocity changes increase and accuracy can increase as well to a point. Numerical
                    // round-off errors can effect this, if set too high. This is important to note if a user has
                    // very high acceleration and/or feedrate requirements for their machine.
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
                        // Reset trapezoid tick cycle counter to make sure that the deceleration is performed the
                        // same every time. Reset to CYCLES_PER_ACCELERATION_TICK/2 to follow the midpoint rule for
                        // an accurate approximation of the deceleration curve. For triangle profiles, down count
                        // from current cycle counter to ensure exact deceleration curve.
                        if (st.getStepEventsCompleted() == currentBlock.getDecelerateAfter()) {
                            if (st.getTrapezoidAdjustedRate() == currentBlock.getNominalRate()) {
                                st.setTrapezoidTickCycleCounter(CYCLES_PER_ACCELERATION_TICK/2); // Trapezoid profile
                            } else {
                                st.setTrapezoidTickCycleCounter(CYCLES_PER_ACCELERATION_TICK-st.getTrapezoidTickCycleCounter()); // Triangle profile
                            }
                        } else {
                            // Iterate cycle counter and check if speeds need to be reduced.
                            if ( iterateTrapezoidCycleCounter() ) {
                                // NOTE: We will only do a full speed reduction if the result is more than the minimum safe
                                // rate, initialized in trapezoid reset as 1.5 x rate_delta. Otherwise, reduce the speed by
                                // half increments until finished. The half increments are guaranteed not to exceed the
                                // CNC acceleration limits, because they will never be greater than rate_delta. This catches
                                // small errors that might leave steps hanging after the last trapezoid tick or a very slow
                                // step rate at the end of a full stop deceleration in certain situations. The half rate
                                // reductions should only be called once or twice per block and create a nice smooth
                                // end deceleration.
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
//                plan_discard_current_block();
            }
        }
//        out_bits ^= settings.invert_mask;  // Apply step and direction invert mask
        busy = false;
    }

    public static void TIM4_IRQHandler() {
        try {
            Thread.sleep(TIM4_timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("STEP_GPIO-B-----X_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(2)));
        log.info("STEP_GPIO-B-----Y_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(3)));
        log.info("STEP_GPIO-B-----Z_STEP_GPIO_PIN-----{}", bitIsTrue(stepPortInvertMask,bit(4)));
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

        threadPoolExecutor.execute(() -> TIM4_IRQHandler());
    }
}
