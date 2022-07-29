package com.zjz.mq.cnc.obj;

import lombok.Data;

import java.util.List;

/**
 * @author zjz
 * @date 2022/7/29 17:08
 */
@Data
public class StepObj {
    private Integer counterX;
    private Integer counterY;
    private Integer counterZ;
    private Integer stepBits;
    private Integer executeStep;
    private Integer stepPulseTime;
    private Integer stepOutbits;
    private Integer dirOutbits;
    private List<Integer> steps;
    private Integer stepCount;
    private Integer execBlockIndex;
    private StBlock exec_block;
    private segmentT exec_segment;
}

@Data
class StBlock {
    private Integer directionBits;
    private List<Integer> steps;
    private Integer stepEventCount;
}

@Data
class segmentT {
    private Integer nStep;
    private Integer stBlockIndex;
    private Integer cyclesPerTick;
    private Integer amassLevel;
    private Integer prescaler;
}
