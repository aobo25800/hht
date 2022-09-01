package com.zjz.mq.cnc;

import com.zjz.mq.cnc.grbl.Planner;

/**
 * @author zjz
 * @date 2022/3/3 15:22
 */
public class CNCApplication {

    public static void main(String[] args) {
        Planner.planInit();

        Planner.planBufferLine(15, 18, 0, 300, false);
        Planner.planBufferLine(20, 15, 0, 300, false);
        Planner.planBufferLine(20, -15, 0, 300, false);
        Planner.planBufferLine(-20, -15, 0, 300, false);
        Planner.planBufferLine(-20, 15, 0, 300, false);
        Planner.planBufferLine(20, -15, 0, 300, false);
        Planner.planBufferLine(-20, -15, 0, 300, false);

    }
}
