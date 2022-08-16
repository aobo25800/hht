package com.zjz.mq.cnc.grbl;

import com.zjz.mq.cnc.obj.BlockT;
import com.zjz.mq.cnc.constant.SystemConstant;
import com.zjz.mq.cnc.obj.PlannerT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjz
 * @date 2022/8/5 16:35
 */
public class Planner {

    private static BlockT[] block_buffer = {new BlockT(), new BlockT()};

    private static int block_buffer_head = 0;
    private static int block_buffer_tail = 0;
    private static int next_buffer_head = 0;

    public static void plan_buffer_line(float x, float y, float z, float feed_rate, boolean invert_feed_rate) {

        PlannerT plannerT = new PlannerT();

        int[] position = plannerT.getPosition();
        // 初始坐标点
        position[SystemConstant.X_AXIS] = 0;
        position[SystemConstant.Y_AXIS] = 0;
        position[SystemConstant.Z_AXIS] = 0;

        // Prepare to set up new block
        BlockT block = block_buffer[block_buffer_head];

        // Calculate target position in absolute steps
        // lround() 四舍五入
        int[] target = new int[3];
        target[SystemConstant.X_AXIS] = Math.round(x * 250);
        target[SystemConstant.Y_AXIS] = Math.round(y * 250);
        target[SystemConstant.Z_AXIS] = Math.round(z * 250);

        // Compute direction bits for this block
        block.setDirection_bits(0);
        int direction = 0;
        if (target[SystemConstant.X_AXIS] < position[SystemConstant.X_AXIS]) {
            block.setDirection_bits(direction |= (1<<5));
        }
        if (target[SystemConstant.Y_AXIS] < position[SystemConstant.Y_AXIS]) {
            block.setDirection_bits(direction |= (1<<6));
        }
        if (target[SystemConstant.Z_AXIS] < position[SystemConstant.Z_AXIS]) {
            block.setDirection_bits(direction |= (1<<7));
        }

        // Number of steps for each axis
        // labs() 绝对值
        block.setSteps_x(Math.abs(target[SystemConstant.X_AXIS] - position[SystemConstant.X_AXIS]));
        block.setSteps_y(Math.abs(target[SystemConstant.Y_AXIS] - position[SystemConstant.Y_AXIS]));
        block.setSteps_z(Math.abs(target[SystemConstant.Z_AXIS] - position[SystemConstant.Z_AXIS]));
        block.setStep_event_count(Math.max(block.getSteps_x(), Math.max(block.getSteps_y(), block.getSteps_z())));

        // Bail if this is a zero-length block
        if (block.getStep_event_count() == 0) { return; };

        // Compute path vector in terms of absolute step target and current positions
        // 当前位置到下位置的增量坐标
        float[] delta_mm = new float[3];
        delta_mm[SystemConstant.X_AXIS] = (float) (target[SystemConstant.X_AXIS] - position[SystemConstant.X_AXIS])/250;
        delta_mm[SystemConstant.Y_AXIS] = (float) (target[SystemConstant.Y_AXIS] - position[SystemConstant.Y_AXIS])/250;
        delta_mm[SystemConstant.Z_AXIS] = (float) (target[SystemConstant.Z_AXIS] - position[SystemConstant.Z_AXIS])/250;
        // 对角线长度    （长的平方 + 宽的平方 + 高的平方）开方
        block.setMillimeters(
                (float) Math.sqrt(
                        delta_mm[SystemConstant.X_AXIS] * delta_mm[SystemConstant.X_AXIS] +
                        delta_mm[SystemConstant.Y_AXIS] * delta_mm[SystemConstant.Y_AXIS] +
                        delta_mm[SystemConstant.Z_AXIS] * delta_mm[SystemConstant.Z_AXIS]
                )
        );
        float inverse_millimeters = 1.0f/block.getMillimeters();  // Inverse millimeters to remove multiple divides

        // Calculate speed in mm/minute for each axis. No divide by zero due to previous checks.
        // NOTE: Minimum stepper speed is limited by MINIMUM_STEPS_PER_MINUTE in stepper.c
        float inverse_minute;
        if (!invert_feed_rate) {
            inverse_minute = feed_rate * inverse_millimeters;
        } else {
            inverse_minute = (float) 1.0 / feed_rate;
        }
        block.setNominal_speed(
                block.getMillimeters() * inverse_minute
        );
        // ceil() 向上取整
        block.setNominal_rate(
                (int) Math.ceil(block.getStep_event_count() * inverse_minute)
        );

        // 计算梯形发生器的加速度。 根据线的斜率，每步事件的平均行程会发生变化。
        // 对于沿一个轴的线，每步事件的行程等于特定轴上的行程/步。
        // 对于 45 度线，两个轴的步进器可能会为每个步进事件步进。
        // 然后每步行程事件为 sqrt(travel_x^2+travel_y^2)。
        // 要在块之间生成具有恒定加速度的梯形，必须专门为每条线计算 rate_delta 以补偿这种现象：将通用加速度转换为与方向相关的步进速率变化参数
        block.setRate_delta(
                (int) Math.ceil(
                        block.getStep_event_count() *
                        inverse_millimeters *
                        (10.0 * 60 * 60) / (60 * 50)
                )
        );  // (step/min/acceleration_tick)

        // Compute path unit vector
        float[] unit_vec = new float[3];

        unit_vec[SystemConstant.X_AXIS] = delta_mm[SystemConstant.X_AXIS]*inverse_millimeters;
        unit_vec[SystemConstant.Y_AXIS] = delta_mm[SystemConstant.Y_AXIS]*inverse_millimeters;
        unit_vec[SystemConstant.Z_AXIS] = delta_mm[SystemConstant.Z_AXIS]*inverse_millimeters;

        float vmax_junction = 0.0f; // Set default max junction speed

        // Skip first block or when previous_nominal_speed is used as a flag for homing and offset cycles.
        if (plannerT.getPrevious_nominal_speed() > 0.0) {
            // Compute cosine of angle between previous and current path. (prev_unit_vec is negative)
            // NOTE: Max junction velocity is computed without sin() or acos() by trig half angle identity.
            float cos_theta =   - plannerT.getPrevious_unit_vec()[SystemConstant.X_AXIS] * unit_vec[SystemConstant.X_AXIS]
                                - plannerT.getPrevious_unit_vec()[SystemConstant.Y_AXIS] * unit_vec[SystemConstant.Y_AXIS]
                                - plannerT.getPrevious_unit_vec()[SystemConstant.Z_AXIS] * unit_vec[SystemConstant.Z_AXIS] ;

            // Skip and use default max junction speed for 0 degree acute junction.
            if (cos_theta < 0.95) {
                vmax_junction = Math.min(plannerT.getPrevious_nominal_speed(), block.getNominal_speed());
                // Skip and avoid divide by zero for straight junctions at 180 degrees. Limit to min() of nominal speeds.
                if (cos_theta > -0.95) {
                    // Compute maximum junction velocity based on maximum acceleration and junction deviation
                    float sin_theta_d2 = (float) Math.sqrt(0.5*(1.0-cos_theta)); // Trig half angle identity. Always positive.
                    vmax_junction = (float) Math.min(vmax_junction,
                            Math.sqrt((10.0*60*60) * 0.05 * sin_theta_d2/(1.0-sin_theta_d2))
                    );
                }
            }
        }
        block.setMax_entry_speed(vmax_junction);

        // Initialize block entry speed. Compute based on deceleration to user-defined MINIMUM_PLANNER_SPEED.
        float v_allowable = max_allowable_speed(-10.0f*60*60, 0.0f, block.getMillimeters());
        block.setEntry_speed(Math.min(vmax_junction, v_allowable));

        if (block.getNominal_speed() <= v_allowable) { block.setNominal_length_flag(true); }
        else { block.setNominal_length_flag(false); }
        block.setRecalculate_flag(true);// Always calculate trapezoid for new block

        // Update previous path unit_vector and nominal speed
//        memcpy(pl.previous_unit_vec, unit_vec, sizeof(unit_vec)); // pl.previous_unit_vec[] = unit_vec[]
        plannerT.setPrevious_nominal_speed(block.getNominal_speed());

        // Update buffer head and next buffer head indices
        block_buffer_head = next_buffer_head;
        next_buffer_head = next_block_index(block_buffer_head);

        // Update planner position
//        memcpy(pl.position, target, sizeof(target)); // pl.position[] = target[]

        planner_recalculate();
    }

    public static float max_allowable_speed(float acceleration, float target_velocity, float distance)
    {
        return (float) Math.sqrt(target_velocity*target_velocity-2*acceleration*distance);
    }

    public static void planner_recalculate()
    {
        planner_reverse_pass();
        planner_recalculate_trapezoids();
        planner_forward_pass();
    }

    public static void planner_reverse_pass()
    {
        int block_index = block_buffer_head;
        BlockT[] block = new BlockT[3];
        while(block_index != block_buffer_tail) {
            block_index = prev_block_index(block_index);
            block[2]= block[1];
            block[1]= block[0];
            block[0] = block_buffer[block_index];
            planner_reverse_pass_kernel(block[0], block[1], block[2]);
        }
        // Skip buffer tail/first block to prevent over-writing the initial entry speed.
    }

    public static int prev_block_index(int block_index)
    {
        if (block_index == 0) { block_index = 18; }
        block_index--;
        return(block_index);
    }

    public static void planner_reverse_pass_kernel(BlockT previous, BlockT current, BlockT next)
    {

        if (next != null) {

            if (current.getEntry_speed() != current.getMax_entry_speed()) {

                if ((!current.getNominal_length_flag()) && (current.getMax_entry_speed() > next.getEntry_speed())) {
                    current.setEntry_speed(
                            Math.min( current.getMax_entry_speed(),
                                    max_allowable_speed((float) -(10.0*60*60), next.getEntry_speed(), current.getMillimeters()))
                    );
                } else {
                    current.setEntry_speed(current.getMax_entry_speed());
                }
                current.setRecalculate_flag(true);

            }
        } // Skip last block. Already initialized and set for recalculation.
    }

    public static void planner_recalculate_trapezoids()
    {
        int block_index = block_buffer_tail;
        BlockT current;
        BlockT next = new BlockT();

        while(block_index != block_buffer_head) {
            current = next;
            next = block_buffer[block_index];
            if (current != null) {
                // Recalculate if current block entry or exit junction speed has changed.
                if (current.getRecalculate_flag() || next.getRecalculate_flag()) {
                    // NOTE: Entry and exit factors always > 0 by all previous logic operations.
                    calculate_trapezoid_for_block(current, current.getEntry_speed()/current.getNominal_speed(),
                            next.getEntry_speed()/current.getNominal_speed());
                    current.setRecalculate_flag(false); // Reset current only to ensure next trapezoid is computed
                }
            }
            block_index = next_block_index( block_index );
        }
        // Last/newest block in buffer. Exit speed is set with MINIMUM_PLANNER_SPEED. Always recalculated.
        calculate_trapezoid_for_block(next, next.getEntry_speed()/next.getNominal_speed(),
                (float) (0.0/next.getNominal_speed()));
        next.setRecalculate_flag(false);
    }
    public static int next_block_index(int block_index)
    {
        block_index++;
        if (block_index == 18) { block_index = 0; }
        return block_index;
    }

    public static void calculate_trapezoid_for_block(BlockT block, float entry_factor, float exit_factor)
    {
        block.setInitial_rate(
                (int) Math.ceil(block.getNominal_rate() * entry_factor)
        ); // (step/min)
        block.setFinal_rate(
                (int) Math.ceil(block.getNominal_rate() * exit_factor)
        ); // (step/min)
        int acceleration_per_minute = (int) (block.getRate_delta()*50*60.0); // (step/min^2)
        int accelerate_steps =
                (int) Math.ceil(estimate_acceleration_distance(block.getInitial_rate(), block.getNominal_rate(), acceleration_per_minute));
        int decelerate_steps =
                (int) Math.floor(estimate_acceleration_distance(block.getNominal_rate(), block.getFinal_rate(), -acceleration_per_minute));

        int plateau_steps = block.getStep_event_count()-accelerate_steps-decelerate_steps;

        if (plateau_steps < 0) {
            accelerate_steps = (int) Math.ceil(
                    intersection_distance(block.getInitial_rate(), block.getFinal_rate(), acceleration_per_minute, block.getStep_event_count()));
            accelerate_steps = Math.max(accelerate_steps,0); // Check limits due to numerical round-off
            accelerate_steps = Math.min(accelerate_steps,block.getStep_event_count());
            plateau_steps = 0;
        }

        block.setAccelerate_until(accelerate_steps);
        block.setDecelerate_after(accelerate_steps+plateau_steps);
    }

    public static float estimate_acceleration_distance(float initial_rate, float target_rate, float acceleration)
    {
        return( (target_rate*target_rate-initial_rate*initial_rate)/(2*acceleration) );
    }

    public static float intersection_distance(float initial_rate, float final_rate, float acceleration, float distance)
    {
        return( (2*acceleration*distance-initial_rate*initial_rate+final_rate*final_rate)/(4*acceleration) );
    }

    public static void planner_forward_pass()
    {
        int block_index = block_buffer_tail;
        BlockT[] block = new BlockT[3];

        while(block_index != block_buffer_head) {
            block[0] = block[1];
            block[1] = block[2];
            block[2] = block_buffer[block_index];
            planner_forward_pass_kernel(block[0],block[1],block[2]);
            block_index = next_block_index( block_index );
        }
        planner_forward_pass_kernel(block[1], block[2], null);
    }

    public static void planner_forward_pass_kernel(BlockT previous, BlockT current, BlockT next)
    {

        if (!previous.getNominal_length_flag()) {
            if (previous.getEntry_speed() < current.getEntry_speed()) {
                float entry_speed = Math.min( current.getEntry_speed(),
                        max_allowable_speed((float) -(10.0*60*60),previous.getEntry_speed(),previous.getMillimeters()) );

                // Check for junction speed change
                if (current.getEntry_speed() != entry_speed) {
                    current.setEntry_speed(entry_speed);
                    current.setRecalculate_flag(true);
                }
            }
        }
    }
}
