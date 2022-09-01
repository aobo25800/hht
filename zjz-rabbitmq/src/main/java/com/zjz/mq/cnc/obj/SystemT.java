package com.zjz.mq.cnc.obj;

import lombok.Data;

/**
 * @author zjz
 * @date 2022/9/1 14:59
 */
@Data
public class SystemT {
    private int abort;                 // System abort flag. Forces exit back to main loop for reset.
    private int state;                 // Tracks the current state of Grbl.
    private int execute;      // Global system runtime executor bitflag variable. See EXEC bitmasks.
    private int position[] = new int[3];      // Real-time machine (aka home) position vector in steps.
    // NOTE: This may need to be a volatile variable, if problems arise.
    private int auto_start;            // Planner auto-start flag. Toggled off during feed hold. Defaulted by settings.

}
