package com.zjz.mq.cnc.constant;

/**
 * @author zjz
 * @date 2022/3/4 12:08
 */
public interface SystemConstant {
    int COMMENT_NONE = 0;
    /**
     * 系统运行状态
     */
    int STATE_ALARM = 0;

    /**
     * 轴号
     */
    int X_AXIS = 0;
    int Y_AXIS = 1;
    int Z_AXIS = 2;

    /**
     * 默认每毫米对应的步数
     */
    int DEFAULT_X_STEPS_PER_MM = 250;

    int X_STEP_BIT = 2;  // Uno Digital Pin 2
    int Y_STEP_BIT = 3;  // Uno Digital Pin 3
    int Z_STEP_BIT = 4;  // Uno Digital Pin 4
    int X_DIRECTION_BIT = 5;  // Uno Digital Pin 5
    int Y_DIRECTION_BIT = 6;  // Uno Digital Pin 6
    int Z_DIRECTION_BIT = 7;  // Uno Digital Pin 7

    /**
     * 进给保持标志
     */
    int STATE_HOLD = 4;
}
