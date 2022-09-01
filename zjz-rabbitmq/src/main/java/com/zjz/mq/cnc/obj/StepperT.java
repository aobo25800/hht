package com.zjz.mq.cnc.obj;

import lombok.Data;

import java.util.List;

/**
 * @author zjz
 * @date 2022/7/29 17:08
 */
@Data
public class StepperT {
        int counterX;
        int counterY;
        int counterZ;
        int eventCount;
        int stepEventsCompleted;
        long cyclesPerStepEvent;
        int trapezoidTickCycleCounter;
        int trapezoidAdjustedRate;
        int minSafeRate;
}

