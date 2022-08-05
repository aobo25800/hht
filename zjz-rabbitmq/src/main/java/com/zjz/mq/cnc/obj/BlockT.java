package com.zjz.mq.cnc.obj;

import lombok.Data;

/**
 * @author zjz
 * @date 2022/8/5 16:36
 */
@Data
public class BlockT {

    // Fields used by the bresenham algorithm for tracing the line
    private int direction_bits;            // The direction bit set for this block (refers to *_DIRECTION_BIT in config.h)
    private int steps_x, steps_y, steps_z; // Step count along each axis
    private int step_event_count;          // The number of step events required to complete this block

    // Fields used by the motion planner to manage acceleration
    private float nominal_speed;               // The nominal speed for this block in mm/min
    private float entry_speed;                 // Entry speed at previous-current block junction in mm/min
    private float max_entry_speed;             // Maximum allowable junction entry speed in mm/min
    private float millimeters;                 // The total travel of this block in mm
    private boolean recalculate_flag;           // Planner flag to recalculate trapezoids on entry junction
    private boolean nominal_length_flag;        // Planner flag for nominal speed always reached

    // Settings for the trapezoid generator
    private int initial_rate;              // The step rate at start of block
    private int final_rate;                // The step rate at end of block
    private int rate_delta;                 // The steps/minute to add or subtract when changing speed (must be positive)
    private int accelerate_until;          // The index of the step event on which to stop acceleration
    private int decelerate_after;          // The index of the step event on which to start decelerating
    private int nominal_rate;              // The nominal step rate for this block in step_events/minute

}
