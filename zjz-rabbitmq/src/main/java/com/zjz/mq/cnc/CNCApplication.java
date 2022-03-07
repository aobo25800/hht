package com.zjz.mq.cnc;

/**
 * @author zjz
 * @date 2022/3/3 15:22
 */
public class CNCApplication {
    public static void main(String[] args) {

//        serial_init();   // Setup serial baud rate and interrupts
//        settings_init(); // Load Grbl settings from EEPROM
//        stepper_init();  // Configure stepper pins and interrupt timers
//        system_init();   // Configure pinout pins and pin-change interrupt
//
//        memset(&sys, 0, sizeof(system_t));  // Clear all system variables
//        sys.abort = true;   // Set abort to complete initialization
//        sei(); // Enable interrupts
//
//        for (; ; ) {
//
//            // TODO: Separate configure task that require interrupts to be disabled, especially upon
//            // a system abort and ensuring any active interrupts are cleanly reset.
//
//            // Reset Grbl primary systems.
//            serial_reset_read_buffer(); // Clear serial read buffer
//            gc_init(); // Set g-code parser to default state
//            spindle_init();
//            coolant_init();
//            limits_init();
//            probe_init();
//            plan_reset(); // Clear block buffer and planner variables
//            st_reset(); // Clear stepper subsystem variables.
//
//            // Sync cleared gcode and planner positions to current system position.
//            plan_sync_position();
//            gc_sync_position();
//
//            // Reset system variables.
//            sys.abort = false;
//            sys_rt_exec_state = 0;
//            sys_rt_exec_alarm = 0;
//            sys.suspend = false;
//            sys.soft_limit = false;
//
//            // Start Grbl main loop. Processes program inputs and executes them.
//            protocol_main_loop();
//        }
    }
}
