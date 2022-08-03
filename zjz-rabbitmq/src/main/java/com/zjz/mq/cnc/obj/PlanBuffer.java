package com.zjz.mq.cnc.obj;

import lombok.Data;

/**
 * @author zjz
 * @date 2022/8/3 14:32
 */
@Data
public class PlanBuffer {
        int direction_bits;
        int steps[] = new int[3];
        int step_event_count;
        float entry_speed_sqr;
        float max_entry_speed_sqr;
        float max_junction_speed_sqr;
        float nominal_speed_sqr;
        double acceleration;
        float millimeters;
        int line_number;

}
