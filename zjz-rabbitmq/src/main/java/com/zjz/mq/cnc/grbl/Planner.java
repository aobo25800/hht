package com.zjz.mq.cnc.grbl;

import com.zjz.mq.cnc.obj.BlockT;
import com.zjz.mq.cnc.constant.SystemConstant;
import com.zjz.mq.cnc.obj.PlannerT;


/**
 * @author zjz
 * @date 2022/8/5 16:35
 */
public class Planner {

    private static final PlannerT plannerT = new PlannerT();
    private static final BlockT[] blockBuffer = new BlockT[18];

    private static int blockBufferHead = 0;
    private static int blockBufferTail = 0;
    private static int nextBufferHead = 0;

    public static char[] chars = "0123456789ABCDEF".toCharArray();

    public static void planInit()
    {
        blockBufferTail = blockBufferHead;
        nextBufferHead = nextBlockIndex(blockBufferHead);
    }

    public static void planBufferLine(float x, float y, float z, float feedRate, boolean invertFeedRate) {

        int[] position = plannerT.getPosition();

        // Prepare to set up new block
        BlockT block = new BlockT();

        // Calculate target position in absolute steps DEFAULT_X_STEPS_PER_MM
        // l round() 四舍五入
        int[] target = new int[3];
        target[SystemConstant.X_AXIS] = Math.round(x * SystemConstant.DEFAULT_X_STEPS_PER_MM);  // DEFAULT_X_STEPS_PER_MM 可配置参数 默认每毫米对应的步数
        target[SystemConstant.Y_AXIS] = Math.round(y * SystemConstant.DEFAULT_X_STEPS_PER_MM);  // DEFAULT_X_STEPS_PER_MM 可配置参数 默认每毫米对应的步数
        target[SystemConstant.Z_AXIS] = Math.round(z * SystemConstant.DEFAULT_X_STEPS_PER_MM);  // DEFAULT_X_STEPS_PER_MM 可配置参数 默认每毫米对应的步数

        // Compute direction bits for this block
        block.setDirectionBits(0);
        int direction = 0;
        if (target[SystemConstant.X_AXIS] < position[SystemConstant.X_AXIS]) {
            block.setDirectionBits(direction |= (1 << 5));
        }
        if (target[SystemConstant.Y_AXIS] < position[SystemConstant.Y_AXIS]) {
            block.setDirectionBits(direction |= (1 << 6));
        }
        if (target[SystemConstant.Z_AXIS] < position[SystemConstant.Z_AXIS]) {
            block.setDirectionBits(direction |= (1 << 7));
        }

        // 每个轴增量步数
        // labs() 绝对值
        block.setStepsX(Math.abs(target[SystemConstant.X_AXIS] - position[SystemConstant.X_AXIS]));
        block.setStepsY(Math.abs(target[SystemConstant.Y_AXIS] - position[SystemConstant.Y_AXIS]));
        block.setStepsZ(Math.abs(target[SystemConstant.Z_AXIS] - position[SystemConstant.Z_AXIS]));
        // 记录当前线段最大步数
        block.setStepEventCount(Math.max(block.getStepsX(), Math.max(block.getStepsY(), block.getStepsZ())));

        // Bail if this is a zero-length block
        if (block.getStepEventCount() == 0) { return; }

        // Compute path vector in terms of absolute step target and current positions
        // 当前位置到下位置的增量长度
        float[] deltaMm = new float[3];
        deltaMm[SystemConstant.X_AXIS] = (float) (target[SystemConstant.X_AXIS] - position[SystemConstant.X_AXIS])/250;
        deltaMm[SystemConstant.Y_AXIS] = (float) (target[SystemConstant.Y_AXIS] - position[SystemConstant.Y_AXIS])/250;
        deltaMm[SystemConstant.Z_AXIS] = (float) (target[SystemConstant.Z_AXIS] - position[SystemConstant.Z_AXIS])/250;
        // 对角线长度    （长的平方 + 宽的平方 + 高的平方）开方
        block.setMillimeters(
                (float) Math.sqrt(
                        deltaMm[SystemConstant.X_AXIS] * deltaMm[SystemConstant.X_AXIS] +
                        deltaMm[SystemConstant.Y_AXIS] * deltaMm[SystemConstant.Y_AXIS] +
                        deltaMm[SystemConstant.Z_AXIS] * deltaMm[SystemConstant.Z_AXIS]));
        //
        float inverseMillimeters = 1.0f/block.getMillimeters();  // Inverse millimeters to remove multiple divides

        // Calculate speed in mm/minute for each axis. No divide by zero due to previous checks.
        // NOTE: Minimum stepper speed is limited by MINIMUM_STEPS_PER_MINUTE in stepper.c
        float inverseMinute;
        if (!invertFeedRate) {
            inverseMinute = feedRate * inverseMillimeters;
        } else {
            inverseMinute = (float) 1.0 / feedRate;
        }
        block.setNominalSpeed(
                block.getMillimeters() * inverseMinute
        );
        // ceil() 向上取整
        block.setNominalRate(
                (int) Math.ceil(block.getStepEventCount() * inverseMinute)
        );

        // 计算梯形发生器的加速度。 根据线的斜率，每步事件的平均行程会发生变化。
        // 对于沿一个轴的线，每步事件的行程等于特定轴上的行程/步。
        // 对于 45 度线，两个轴的步进器可能会为每个步进事件步进。
        // 然后每步行程事件为 sqrt(travel_x^2+travel_y^2)。
        // 要在块之间生成具有恒定加速度的梯形，必须专门为每条线计算 rate_delta 以补偿这种现象：将通用加速度转换为与方向相关的步进速率变化参数
        block.setRateDelta(
                (int) Math.ceil(
                        block.getStepEventCount() *
                        inverseMillimeters *
                        (10.0 * 60 * 60) / (60 * 50)
                )
        );  // (step/min/acceleration_tick)

        // Compute path unit vector
        float[] unitVec = new float[3];
        unitVec[SystemConstant.X_AXIS] = deltaMm[SystemConstant.X_AXIS] * inverseMillimeters;
        unitVec[SystemConstant.Y_AXIS] = deltaMm[SystemConstant.Y_AXIS] * inverseMillimeters;
        unitVec[SystemConstant.Z_AXIS] = deltaMm[SystemConstant.Z_AXIS] * inverseMillimeters;

        float maxJunction = 0.0f; // Set default max junction speed

        // Skip first block or when previous_nominal_speed is used as a flag for homing and offset cycles.
        if (plannerT.getPreviousNominalSpeed() > 0.0) {
            // Compute cosine of angle between previous and current path. (prev_unitVec is negative)
            // NOTE: Max junction velocity is computed without sin() or acos() by trig half angle identity.
            float cosTheta =   - plannerT.getPreviousUnitVec()[SystemConstant.X_AXIS] * unitVec[SystemConstant.X_AXIS]
                                - plannerT.getPreviousUnitVec()[SystemConstant.Y_AXIS] * unitVec[SystemConstant.Y_AXIS]
                                - plannerT.getPreviousUnitVec()[SystemConstant.Z_AXIS] * unitVec[SystemConstant.Z_AXIS] ;

            // Skip and use default max junction speed for 0 degree acute junction.
            if (cosTheta < 0.95) {
                maxJunction = Math.min(plannerT.getPreviousNominalSpeed(), block.getNominalSpeed());
                // Skip and avoid divide by zero for straight junctions at 180 degrees. Limit to min() of nominal speeds.
                if (cosTheta > -0.95) {
                    // Compute maximum junction velocity based on maximum acceleration and junction deviation
                    float sinThetaD2 = (float) Math.sqrt(0.5*(1.0-cosTheta)); // Trig half angle identity. Always positive.
                    maxJunction = (float) Math.min(maxJunction,
                            Math.sqrt((10.0*60*60) * 0.05 * sinThetaD2/(1.0-sinThetaD2))
                    );
                }
            }
        }
        block.setMaxEntrySpeed(maxJunction);

        // Initialize block entry speed. Compute based on deceleration to user-defined MINIMUM_PLANNER_SPEED.
        float vAllowable = maxAllowableSpeed(-10.0f*60*60, 0.0f, block.getMillimeters());
        block.setEntrySpeed(Math.min(maxJunction, vAllowable));

        block.setNominalLengthFlag(block.getNominalSpeed() <= vAllowable);
        block.setRecalculateFlag(true);// Always calculate trapezoid for new block

        blockBuffer[blockBufferHead] = block;
        plannerT.setPreviousUnitVec(unitVec);
        plannerT.setPreviousNominalSpeed(block.getNominalSpeed());

        // Update buffer head and next buffer head indices
        blockBufferHead = nextBufferHead;
        nextBufferHead = nextBlockIndex(blockBufferHead);

        // Update planner position
        plannerT.setPosition(target);

        plannerRecalculate();
//        block_buffer_tail ++ ;
        System.out.println(block);
//        String s = strToHex(block.toString());
//        System.out.println(s);
    }

    public static float maxAllowableSpeed(float acceleration, float targetVelocity, float distance)
    {
        return (float) Math.sqrt(targetVelocity * targetVelocity - 2 * acceleration * distance);
    }

    public static void plannerRecalculate()
    {
        plannerReversePass();
        plannerRecalculateTrapezoids();
        plannerForwardPass();
    }

    public static void plannerReversePass()
    {
        int blockIndex = blockBufferHead;
        BlockT[] block = new BlockT[3];
        while(blockIndex != blockBufferTail) {
            blockIndex = prevBlockIndex(blockIndex);
            block[2]= block[1];
            block[1]= block[0];
            block[0] = blockBuffer[blockIndex];
            plannerReversePassKernel(block[0], block[1], block[2]);
        }
        // Skip buffer tail/first block to prevent over-writing the initial entry speed.
    }

    public static int prevBlockIndex(int blockIndex)
    {
        if (blockIndex == 0) { blockIndex = 18; }
        blockIndex--;
        return(blockIndex);
    }

    public static void plannerReversePassKernel(BlockT previous, BlockT current, BlockT next)
    {

        if (current == null) {
            return;
        }  // Cannot operate on nothing.

        if (next != null) {

            if (current.getEntrySpeed() != current.getMaxEntrySpeed()) {

                if ((!current.getNominalLengthFlag()) && (current.getMaxEntrySpeed() > next.getEntrySpeed())) {
                    current.setEntrySpeed(
                            Math.min( current.getMaxEntrySpeed(),
                                    maxAllowableSpeed((float) -(10.0*60*60), next.getEntrySpeed(), current.getMillimeters()))
                    );
                } else {
                    current.setEntrySpeed(current.getMaxEntrySpeed());
                }
                current.setRecalculateFlag(true);

            }
        } // Skip last block. Already initialized and set for recalculation.
    }

    public static void plannerRecalculateTrapezoids()
    {
        int blockIndex = blockBufferTail;
        BlockT current;
        BlockT next = null;

        while(blockIndex != blockBufferHead) {
            current = next;
            next = blockBuffer[blockIndex];
            if (current != null) {
                // Recalculate if current block entry or exit junction speed has changed.
                if (current.getRecalculateFlag() || next.getRecalculateFlag()) {
                    // NOTE: Entry and exit factors always > 0 by all previous logic operations.
                    calculateTrapezoidForBlock(current,
                            current.getEntrySpeed()/current.getNominalSpeed(),
                            next.getEntrySpeed()/current.getNominalSpeed());
                    current.setRecalculateFlag(false); // Reset current only to ensure next trapezoid is computed
                }
            }
            blockIndex = nextBlockIndex(blockIndex);
        }
        if (next != null) {
            // Last/newest block in buffer. Exit speed is set with MINIMUM PLANNER SPEED. Always recalculated.
            calculateTrapezoidForBlock(next, next.getEntrySpeed() / next.getNominalSpeed(),
                    (float) (0.0 / next.getNominalSpeed()));
            next.setRecalculateFlag(false);
        }
    }
    public static int nextBlockIndex(int blockIndex)
    {
        blockIndex++;
        if (blockIndex == 18) { blockIndex = 0; }
        return blockIndex;
    }

    public static void calculateTrapezoidForBlock(BlockT block, float entryFactor, float exitFactor)
    {
        block.setInitialRate(
                (int) Math.ceil(block.getNominalRate() * entryFactor)
        ); // (step/min)
        block.setFinalRate(
                (int) Math.ceil(block.getNominalRate() * exitFactor)
        ); // (step/min)
        int accelerationPerMinute = (int) (block.getRateDelta() * 50 * 60.0); // (step/min^2)
        int accelerateSteps =
                (int) Math.ceil(estimateAccelerationDistance(block.getInitialRate(), block.getNominalRate(), accelerationPerMinute));
        int decelerateSteps =
                (int) Math.floor(estimateAccelerationDistance(block.getNominalRate(), block.getFinalRate(), -accelerationPerMinute));

        int plateauSteps = block.getStepEventCount() - accelerateSteps - decelerateSteps;

        if (plateauSteps < 0) {
            accelerateSteps = (int) Math.ceil(
                    intersectionDistance(block.getInitialRate(), block.getFinalRate(), accelerationPerMinute, block.getStepEventCount()));
            accelerateSteps = Math.max(accelerateSteps,0); // Check limits due to numerical round-off
            accelerateSteps = Math.min(accelerateSteps,block.getStepEventCount());
            plateauSteps = 0;
        }

        block.setAccelerateUntil(accelerateSteps);
        block.setDecelerateAfter(accelerateSteps + plateauSteps);
    }

    public static float estimateAccelerationDistance(float initialRate, float targetRate, float acceleration)
    {
        return (targetRate * targetRate - initialRate * initialRate)/(2 * acceleration);
    }

    public static float intersectionDistance(float initialRate, float finalRate, float acceleration, float distance)
    {
        return( (2 * acceleration * distance - initialRate * initialRate + finalRate * finalRate) / (4 * acceleration));
    }

    public static void plannerForwardPass()
    {
        int blockIndex = blockBufferTail;
        BlockT[] block = new BlockT[3];

        while(blockIndex != blockBufferHead) {
            block[0] = block[1];
            block[1] = block[2];
            block[2] = blockBuffer[blockIndex];
            plannerForwardPassKernel(block[0],block[1],block[2]);
            blockIndex = nextBlockIndex( blockIndex );
        }
        plannerForwardPassKernel(block[1], block[2], null);
    }

    public static void plannerForwardPassKernel(BlockT previous, BlockT current, BlockT next)
    {

        if(previous == null) {
            return;
        }  // Begin planning after buffer tail

        if (!previous.getNominalLengthFlag()) {
            if (previous.getEntrySpeed() < current.getEntrySpeed()) {
                float entrySpeed = Math.min( current.getEntrySpeed(),
                        maxAllowableSpeed((float) -(10.0 * 60 * 60), previous.getEntrySpeed(), previous.getMillimeters()) );

                // Check for junction speed change
                if (current.getEntrySpeed() != entrySpeed) {
                    current.setEntrySpeed(entrySpeed);
                    current.setRecalculateFlag(true);
                }
            }
        }
    }

    public static void setBlockBufferTail(int blockBufferTail) {
        Planner.blockBufferTail = blockBufferTail;
    }

    public static String strToHex(String str) {
        StringBuilder builder = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0xf0) >> 4;
            builder.append(chars[bit]);
            bit = b & 0x0f;
            builder.append(chars[bit]);
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static BlockT planGetCurrentBlock() {
        if (blockBufferHead == blockBufferTail) { return null; }
        return blockBuffer[blockBufferTail];
    }

    public static void planDiscardCurrentBlock()
    {
        if (blockBufferHead != blockBufferTail) {
            blockBufferTail = nextBlockIndex( blockBufferTail );
        }
    }
}
