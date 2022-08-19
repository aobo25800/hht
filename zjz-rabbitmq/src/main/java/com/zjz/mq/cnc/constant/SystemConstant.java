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
}
