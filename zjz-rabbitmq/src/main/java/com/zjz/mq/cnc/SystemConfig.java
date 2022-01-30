package com.zjz.mq.cnc;

/**
 * @author zjz
 * @date 2022/1/29 18:17
 */
public class SystemConfig {
    // 系统状态
    char system_status;
    // 轴定义
    char pulse_X;
    char pulse_Y;
    char pulse_Z;
    char pulse_A;
    char pulse_B;
    char pulse_C;
    // 轴运动方向定义
    char direction_X;
    char direction_Y;
    char direction_Z;
    char direction_A;
    char direction_B;
    char direction_C;
    // 轴限位定义
    char limit_X;
    char limit_Y;
    char limit_Z;
    char limit_A;
    char limit_B;
    char limit_C;
    // 轴运动范围
    char scope_X;
    char scope_Y;
    char scope_Z;
    char scope_A;
    char scope_B;
    char scope_C;
    // 系统使用的字符
    char G = 'G';
    char X = 'X';
}
