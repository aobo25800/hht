package com.zjz.mq.cnc.obj;

import lombok.Data;

/**
 * @author zjz
 * @date 2022/8/11 16:45
 */
@Data
public class PlannerT {

    private int[] position = new int[3];        // The planner position of the tool in absolute steps. Kept separate
                                                // from g-code position for movements requiring multiple line motions,
                                                // i.e. arcs, canned cycles, and backlash compensation.
    private float[] previous_unit_vec = new float[3];      // Unit vector of previous path line segment
    private float previous_nominal_speed;   // Nominal speed of previous path line segment
}
