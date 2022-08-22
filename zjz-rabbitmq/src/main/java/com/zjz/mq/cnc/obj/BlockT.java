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
    private int steps_x, steps_y, steps_z; // Step count along each axis    58-X 59-Y 5A-Z      沿每个轴的步数
    private int step_event_count;          // 完成此块所需的最大步骤事件数

    // Fields used by the motion planner to manage acceleration
    private float nominal_speed;               // The nominal speed for this block in mm/min 此块的标称速度，单位为 mm/min
    private float entry_speed;                 // Entry speed at previous-current block junction in mm/min 前一个当前块连接处的进入速度，单位为 mm/min
    private float max_entry_speed;             // Maximum allowable junction entry speed in mm/min 最大允许结点进入速度，单位为 mm/min
    private float millimeters;                 // The total travel of this block in mm 该块的总行程，单位为 mm
    private Boolean recalculate_flag;           // Planner flag to recalculate trapezoids on entry junction 用于重新计算入口交界处梯形的规划器标志
    private Boolean nominal_length_flag;        // Planner flag for nominal speed always reached 始终达到标称速度的规划器标志

    // Settings for the trapezoid generator
    private int initial_rate;              // The step rate at start of block   梯形中间开始的速率
    private int final_rate;                // The step rate at end of block
    private int rate_delta;                 // 改变速度时要加减的步数/分钟(必须是正的)
    private int accelerate_until;          // 停止加速度的阶跃事件的索引
    private int decelerate_after;          // 开始减速的阶跃事件的索引
    private int nominal_rate;              // 此块的名义步进速率，单位为 step_events/分钟

    @Override
    public String toString() {
        return
                "direction_bits=" + this.direction_bits + "" + ", " +
                "steps_x=" + this.steps_x + "" + ", " +
                "steps_y=" + this.steps_y + "" + ", " +
                "steps_z=" + this.steps_z + "" + ", " +
                "step_event_count=" + this.step_event_count + "" + ", " +
                "nominal_speed=" + this.nominal_speed + "" + ", " +
                "entry_speed=" + this.entry_speed + "" + ", " +
                "max_entry_speed=" + this.max_entry_speed + "" + ", " +
                "millimeters=" + this.millimeters + "" + ", " +
                "recalculate_flag=" + this.recalculate_flag + "" + ", " +
                "nominal_length_flag=" + this.nominal_length_flag + "" + ", " +
                "initial_rate=" + this.initial_rate + "" + ", " +
                "final_rate=" + this.final_rate + "" + ", " +
                "rate_delta=" + this.rate_delta + "" + ", " +
                "accelerate_until=" + this.accelerate_until + "" + ", " +
                "decelerate_after=" + this.decelerate_after + "" + ", " +
                "nominal_rate=" + this.nominal_rate;
    }
}
