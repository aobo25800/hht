package com.zjz.mq.cnc.control;

import com.zjz.mq.cnc.constant.SystemConstant;
import com.zjz.mq.cnc.singleton.SystemActionTrace;

/**
 * @author zjz
 * @date 2022/3/4 12:04
 */
public class InitControl {

    public void main_loop() {
        int comment = SystemConstant.COMMENT_NONE;
        int char_counter = 0;
        int c;

        SystemActionTrace systemActionTrace = new SystemActionTrace();
        // Check for and report alarm state after a reset, error, or an initial power up.
        if (systemActionTrace.getState() == SystemConstant.STATE_ALARM) {
//            report_feedback_message(MESSAGE_ALARM_LOCK);
        } else {
//            // All systems go! But first check for safety door.
//            if (system_check_safety_door_ajar()) {
//                bit_true(sys_rt_exec_state, EXEC_SAFETY_DOOR);
//                protocol_execute_realtime(); // Enter safety door mode. Should return as IDLE state.
//            } else {
//                sys.state = STATE_IDLE; // Set system to ready. Clear all state flags.
//            }
//            system_execute_startup(line); // Execute startup script.
        }

        // ---------------------------------------------------------------------------------
        // Primary loop! Upon a system abort, this exits back to main() to reset the system.
        // ---------------------------------------------------------------------------------

        for (;;) {

            // Process one line of incoming serial data, as the data becomes available. Performs an
            // initial filtering by removing spaces and comments and capitalizing all letters.

            // NOTE: While comment, spaces, and block delete(if supported) handling should technically
            // be done in the g-code parser, doing it here helps compress the incoming data into Grbl's
            // line buffer, which is limited in size. The g-code standard actually states a line can't
            // exceed 256 characters, but the Arduino Uno does not have the memory space for this.
            // With a better processor, it would be very easy to pull this initial parsing out as a
            // seperate task to be shared by the g-code parser and Grbl's system commands.

//            while((c = serial_read()) != SERIAL_NO_DATA) {
//                if ((c == '\n') || (c == '\r')) { // End of line reached
//                    line[char_counter] = 0; // Set string termination character.
//                    protocol_execute_line(line); // Line is complete. Execute it!
//                    comment = COMMENT_NONE;
//                    char_counter = 0;
//                } else {
//                    if (comment != COMMENT_NONE) {
//                        // Throw away all comment characters
//                        if (c == ')') {
//                            // End of comment. Resume line. But, not if semicolon type comment.
//                            if (comment == COMMENT_TYPE_PARENTHESES) { comment = COMMENT_NONE; }
//                        }
//                    } else {
//                        if (c <= ' ') {
//                            // Throw away whitepace and control characters
//                        } else if (c == '/') {
//                            // Block delete NOT SUPPORTED. Ignore character.
//                            // NOTE: If supported, would simply need to check the system if block delete is enabled.
//                        } else if (c == '(') {
//                            // Enable comments flag and ignore all characters until ')' or EOL.
//                            // NOTE: This doesn't follow the NIST definition exactly, but is good enough for now.
//                            // In the future, we could simply remove the items within the comments, but retain the
//                            // comment control characters, so that the g-code parser can error-check it.
//                            comment = COMMENT_TYPE_PARENTHESES;
//                        } else if (c == ';') {
//                            // NOTE: ';' comment to EOL is a LinuxCNC definition. Not NIST.
//                            comment = COMMENT_TYPE_SEMICOLON;
//
//                            // TODO: Install '%' feature
//                            // } else if (c == '%') {
//                            // Program start-end percent sign NOT SUPPORTED.
//                            // NOTE: This maybe installed to tell Grbl when a program is running vs manual input,
//                            // where, during a program, the system auto-cycle start will continue to execute
//                            // everything until the next '%' sign. This will help fix resuming issues with certain
//                            // functions that empty the planner buffer to execute its task on-time.
//
//                        } else if (char_counter >= (LINE_BUFFER_SIZE-1)) {
//                            // Detect line buffer overflow. Report error and reset line buffer.
//                            report_status_message(STATUS_OVERFLOW);
//                            comment = COMMENT_NONE;
//                            char_counter = 0;
//                        } else if (c >= 'a' && c <= 'z') { // Upcase lowercase
//                            line[char_counter++] = c-'a'+'A';
//                        } else {
//                            line[char_counter++] = c;
//                        }
//                    }
//                }
//            }
//
//            // If there are no more characters in the serial read buffer to be processed and executed,
//            // this indicates that g-code streaming has either filled the planner buffer or has
//            // completed. In either case, auto-cycle start, if enabled, any queued moves.
//            protocol_auto_cycle_start();
//
//            protocol_execute_realtime();  // Runtime command check point.
//            if (sys.abort) { return; } // Bail to main() program loop to reset system.
        }

//  return; /* Never reached */
    }
}
