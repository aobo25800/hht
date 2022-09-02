package com.zjz.mq.cnc;

import com.zjz.mq.cnc.grbl.Planner;

/**
 * @author zjz
 * @date 2022/3/3 15:22
 */
public class CNCApplication {

    public static void main(String[] args) {
        Planner.planInit();
        // 1
        Planner.planBufferLine(15, 18, 0, 300, false);
        // 2
        Planner.planBufferLine(20, 15, 0, 300, false);
        // 3
        Planner.planBufferLine(20, -15, 0, 300, false);
        // 4
        Planner.planBufferLine(-20, -15, 0, 300, false);
        // 5
        Planner.planBufferLine(-20, 15, 0, 300, false);
        // 6
        Planner.planBufferLine(20, -15, 0, 300, false);
        // 7
        Planner.planBufferLine(-20, -15, 0, 300, false);
        // 8
        Planner.planBufferLine(15, 18, 0, 300, false);

    }
}
