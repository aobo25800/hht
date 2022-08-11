package com.zjz.mq.cnc.grbl;

import com.zjz.mq.cnc.obj.BlockT;
import com.zjz.mq.cnc.constant.SystemConstant;
import com.zjz.mq.cnc.obj.PlannerT;

/**
 * @author zjz
 * @date 2022/8/5 16:35
 */
public class Planner {

    public static void plan_buffer_line(float x, float y, float z, float feed_rate, boolean invert_feed_rate) {

        PlannerT plannerT = new PlannerT();

        int[] position = plannerT.getPosition();
        // 初始坐标点
        position[SystemConstant.X_AXIS] = 0;
        position[SystemConstant.Y_AXIS] = 0;
        position[SystemConstant.Z_AXIS] = 0;

        // Prepare to set up new block
        BlockT block = new BlockT();

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

        // Compute the acceleration rate for the trapezoid generator. Depending on the slope of the line
        // average travel per step event changes. For a line along one axis the travel per step event
        // is equal to the travel/step in the particular axis. For a 45 degree line the steppers of both
        // axes might step for every step event. Travel per step event is then sqrt(travel_x^2+travel_y^2).
        // To generate trapezoids with constant acceleration between blocks the rate_delta must be computed
        // specifically for each line to compensate for this phenomenon:
        // Convert universal acceleration for direction-dependent stepper rate change parameter
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

        // Compute maximum allowable entry speed at junction by centripetal acceleration approximation.
        // Let a circle be tangent to both previous and current path line segments, where the junction
        // deviation is defined as the distance from the junction to the closest edge of the circle,
        // colinear with the circle center. The circular segment joining the two paths represents the
        // path of centripetal acceleration. Solve for max velocity based on max acceleration about the
        // radius of the circle, defined indirectly by junction deviation. This may be also viewed as
        // path width or max_jerk in the previous grbl version. This approach does not actually deviate
        // from path, but used as a robust way to compute cornering speeds, a s it takes into account the
        // nonlinearities of both the junction angle and junction velocity.
        // NOTE: This is basically an exact path mode (G61), but it doesn't come to a complete stop unless
        // the junction deviation value is high. In the future, if continuous mode (G64) is desired, the
        // math here is exactly the same. Instead of motioning all the way to junction point, the machine
        // will just need to follow the arc circle defined above and check if the arc radii are no longer
        // than half of either line segment to ensure no overlapping. Right now, the Arduino likely doesn't
        // have the horsepower to do these calculations at high feed rates.
//        float vmax_junction = MINIMUM_PLANNER_SPEED; // Set default max junction speed

//        // Skip first block or when previous_nominal_speed is used as a flag for homing and offset cycles.
//        if ((block_buffer_head != block_buffer_tail) && (pl.previous_nominal_speed > 0.0)) {
//            // Compute cosine of angle between previous and current path. (prev_unit_vec is negative)
//            // NOTE: Max junction velocity is computed without sin() or acos() by trig half angle identity.
//            float cos_theta =   - pl.previous_unit_vec[SystemConstant.X_AXIS] * unit_vec[SystemConstant.X_AXIS]
//                                - pl.previous_unit_vec[SystemConstant.Y_AXIS] * unit_vec[SystemConstant.Y_AXIS]
//                                - pl.previous_unit_vec[SystemConstant.Z_AXIS] * unit_vec[SystemConstant.Z_AXIS] ;
//
//            // Skip and use default max junction speed for 0 degree acute junction.
//            if (cos_theta < 0.95) {
//                vmax_junction = Math.min(pl.previous_nominal_speed,block.getNominal_speed());
//                // Skip and avoid divide by zero for straight junctions at 180 degrees. Limit to min() of nominal speeds.
//                if (cos_theta > -0.95) {
//                    // Compute maximum junction velocity based on maximum acceleration and junction deviation
//                    float sin_theta_d2 = (float) Math.sqrt(0.5*(1.0-cos_theta)); // Trig half angle identity. Always positive.
//                    vmax_junction = Math.min(vmax_junction,
//                            Math.sqrt(settings.acceleration * settings.junction_deviation * sin_theta_d2/(1.0-sin_theta_d2)) );
//                }
//            }
//        }
//        block.setMax_entry_speed(vmax_junction);
//
//        // Initialize block entry speed. Compute based on deceleration to user-defined MINIMUM_PLANNER_SPEED.
//        float v_allowable = max_allowable_speed(-settings.acceleration,MINIMUM_PLANNER_SPEED,block->millimeters);
//        block.setEntry_speed(Math.min(vmax_junction, v_allowable));
//
//        // Initialize planner efficiency flags
//        // Set flag if block will always reach maximum junction speed regardless of entry/exit speeds.
//        // If a block can de/ac-celerate from nominal speed to zero within the length of the block, then
//        // the current block and next block junction speeds are guaranteed to always be at their maximum
//        // junction speeds in deceleration and acceleration, respectively. This is due to how the current
//        // block nominal speed limits both the current and next maximum junction speeds. Hence, in both
//        // the reverse and forward planners, the corresponding block junction speed will always be at the
//        // the maximum junction speed and may always be ignored for any speed reduction checks.
//        if (block.getNominal_speed() <= v_allowable) { block.setNominal_length_flag(true); }
//        else { block.setNominal_length_flag(false); }
//        block.setRecalculate_flag(true);// Always calculate trapezoid for new block
//
//        // Update previous path unit_vector and nominal speed
//        memcpy(pl.previous_unit_vec, unit_vec, sizeof(unit_vec)); // pl.previous_unit_vec[] = unit_vec[]
//        pl.previous_nominal_speed = block->nominal_speed;
//
//        // Update buffer head and next buffer head indices
//        block_buffer_head = next_buffer_head;
//        next_buffer_head = next_block_index(block_buffer_head);
//
//        // Update planner position
//        memcpy(pl.position, target, sizeof(target)); // pl.position[] = target[]

//        planner_recalculate();
    }
}
