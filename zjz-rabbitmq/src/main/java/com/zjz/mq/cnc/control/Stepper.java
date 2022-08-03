package com.zjz.mq.cnc.control;

/**
 * @author zjz
 * @date 2022/7/29 18:22
 */
public class Stepper {
    public void TIM3_IRQHandler() {
//        HW_GPIO_Write(DIRECTION_GPIO,X_DIRECTION_GPIO_PIN,bit_istrue(st.dir_outbits,bit(X_DIRECTION_BIT)));
//        HW_GPIO_Write(DIRECTION_GPIO,Y_DIRECTION_GPIO_PIN,bit_istrue(st.dir_outbits,bit(Y_DIRECTION_BIT)));
//        HW_GPIO_Write(DIRECTION_GPIO,Z_DIRECTION_GPIO_PIN,bit_istrue(st.dir_outbits,bit(Z_DIRECTION_BIT)));
//
//        HW_TIM_PortResetInterrupt_ValueConfig(8,st.step_pulse_time); 	//设置定时器重装值
//        HW_TIM_PortResetInterrupt_Enable();  				//开启定时器
        System.out.println("配置移动方向--");
    }

    public void TIM4_IRQHandler() {

//            TIM_ClearITPendingBit(TIM4, TIM_IT_Update  );  //清除TIM4更新中断标志
//            // 复位步进脉冲控制引脚（不影响方向控制引脚）
//            HW_GPIO_Write(STEP_GPIO,X_STEP_GPIO_PIN,bit_istrue(step_port_invert_mask,bit(X_STEP_BIT)));
//            HW_GPIO_Write(STEP_GPIO,Y_STEP_GPIO_PIN,bit_istrue(step_port_invert_mask,bit(Y_STEP_BIT)));
//            HW_GPIO_Write(STEP_GPIO,Z_STEP_GPIO_PIN,bit_istrue(step_port_invert_mask,bit(Z_STEP_BIT)));
//
//            HW_TIM_PortResetInterrupt_Disable();	//关闭定时器
        System.out.println("移动--");

    }
}
