package com.zjz.mq.cnc.control;

import com.zjz.mq.cnc.obj.PlanBuffer;

/**
 * @author zjz
 * @date 2022/8/2 11:59
 */
public class GCodeParse {

    public void parseLine(char[] line) {
//        char letter;
//        int counter = 0;
//        while (line[counter] != 0) {
//            letter = line[counter];
//            counter ++;
////            if (!read_float(line, &char_counter, &value)) {
////                FAIL(STATUS_BAD_NUMBER_FORMAT);
////            }
//
////            int_value = (uint8_t)(value);
////            mantissa =  (uint16_t)(100*(value - int_value)+0.5); // Compute mantissa for Gxx.x commands.
//
//            switch(letter) {
//                case 'G':
//                    // Determine 'G' command and its modal group
//                    switch(int_value) {
//                        case 10: case 28: case 30: case 92:
//                            // Check for G10/28/30/92 being called with G0/1/2/3/38 on same block.
//                            // * G43.1 is also an axis command but is not explicitly defined this way.
//                            if (mantissa == 0) { // Ignore G28.1, G30.1, and G92.1
//                                if (axis_command) { FAIL(STATUS_GCODE_AXIS_COMMAND_CONFLICT); } // [Axis word/command conflict]
//                                axis_command = AXIS_COMMAND_NON_MODAL;
//                            }
//                            // No break. Continues to next line.
//                        case 4: case 53:
//                            word_bit = MODAL_GROUP_G0;
//                            switch(int_value) {
//                                case 4: gc_block.non_modal_command = NON_MODAL_DWELL; break; // G4
//                                case 10: gc_block.non_modal_command = NON_MODAL_SET_COORDINATE_DATA; break; // G10
//                                case 28:
//                                    switch(mantissa) {
//                                        case 0: gc_block.non_modal_command = NON_MODAL_GO_HOME_0; break;  // G28
//                                        case 10: gc_block.non_modal_command = NON_MODAL_SET_HOME_0; break; // G28.1
//                                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); // [Unsupported G28.x command]
//                                    }
//                                    mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                                    break;
//                                case 30:
//                                    switch(mantissa) {
//                                        case 0: gc_block.non_modal_command = NON_MODAL_GO_HOME_1; break;  // G30
//                                        case 10: gc_block.non_modal_command = NON_MODAL_SET_HOME_1; break; // G30.1
//                                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); // [Unsupported G30.x command]
//                                    }
//                                    mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                                    break;
//                                case 53: gc_block.non_modal_command = NON_MODAL_ABSOLUTE_OVERRIDE; break; // G53
//                                case 92:
//                                    switch(mantissa) {
//                                        case 0: gc_block.non_modal_command = NON_MODAL_SET_COORDINATE_OFFSET; break; // G92
//                                        case 10: gc_block.non_modal_command = NON_MODAL_RESET_COORDINATE_OFFSET; break; // G92.1
//                                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); // [Unsupported G92.x command]
//                                    }
//                                    mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                                    break;
//                            }
//                            break;
//                        case 0: case 1: case 2: case 3: case 38:
//                            // Check for G0/1/2/3/38 being called with G10/28/30/92 on same block.
//                            // * G43.1 is also an axis command but is not explicitly defined this way.
//                            if (axis_command) { FAIL(STATUS_GCODE_AXIS_COMMAND_CONFLICT); } // [Axis word/command conflict]
//                            axis_command = AXIS_COMMAND_MOTION_MODE;
//                            // No break. Continues to next line.
//                        case 80:
//                            word_bit = MODAL_GROUP_G1;
//                            switch(int_value) {
//                                case 0: gc_block.modal.motion = MOTION_MODE_SEEK; break; // G0
//                                case 1: gc_block.modal.motion = MOTION_MODE_LINEAR; break; // G1
//                                case 2: gc_block.modal.motion = MOTION_MODE_CW_ARC; break; // G2
//                                case 3: gc_block.modal.motion = MOTION_MODE_CCW_ARC; break; // G3
//                                case 38:
//                                    switch(mantissa) {
//                                        case 20: gc_block.modal.motion = MOTION_MODE_PROBE_TOWARD; break; // G38.2
//                                        case 30: gc_block.modal.motion = MOTION_MODE_PROBE_TOWARD_NO_ERROR; break; // G38.3
//                                        case 40: gc_block.modal.motion = MOTION_MODE_PROBE_AWAY; break; // G38.4
//                                        case 50: gc_block.modal.motion = MOTION_MODE_PROBE_AWAY_NO_ERROR; break; // G38.5
//                                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); // [Unsupported G38.x command]
//                                    }
//                                    mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                                    break;
//                                case 80: gc_block.modal.motion = MOTION_MODE_NONE; break; // G80
//                            }
//                            break;
//                        case 17: case 18: case 19:
//                            word_bit = MODAL_GROUP_G2;
//                            switch(int_value) {
//                                case 17: gc_block.modal.plane_select = PLANE_SELECT_XY; break;
//                                case 18: gc_block.modal.plane_select = PLANE_SELECT_ZX; break;
//                                case 19: gc_block.modal.plane_select = PLANE_SELECT_YZ; break;
//                            }
//                            break;
//                        case 90: case 91:
//                            if (mantissa == 0) {
//                                word_bit = MODAL_GROUP_G3;
//                                if (int_value == 90) { gc_block.modal.distance = DISTANCE_MODE_ABSOLUTE; } // G90
//                                else { gc_block.modal.distance = DISTANCE_MODE_INCREMENTAL; } // G91
//                            } else {
//                                word_bit = MODAL_GROUP_G4;
//                                if ((mantissa != 10) || (int_value == 90)) { FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); } // [G90.1 not supported]
//                                mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                                // Otherwise, arc IJK incremental mode is default. G91.1 does nothing.
//                            }
//                            break;
//                        case 93: case 94:
//                            word_bit = MODAL_GROUP_G5;
//                            if (int_value == 93) { gc_block.modal.feed_rate = FEED_RATE_MODE_INVERSE_TIME; } // G93
//                            else { gc_block.modal.feed_rate = FEED_RATE_MODE_UNITS_PER_MIN; } // G94
//                            break;
//                        case 20: case 21:
//                            word_bit = MODAL_GROUP_G6;
//                            if (int_value == 20) { gc_block.modal.units = UNITS_MODE_INCHES; }  // G20
//                            else { gc_block.modal.units = UNITS_MODE_MM; } // G21
//                            break;
//                        case 40:
//                            word_bit = MODAL_GROUP_G7;
//                            // NOTE: Not required since cutter radius compensation is always disabled. Only here
//                            // to support G40 commands that often appear in g-code program headers to setup defaults.
//                            // gc_block.modal.cutter_comp = CUTTER_COMP_DISABLE; // G40
//                            break;
//                        case 43: case 49:
//                            word_bit = MODAL_GROUP_G8;
//                            // NOTE: The NIST g-code standard vaguely states that when a tool length offset is changed,
//                            // there cannot be any axis motion or coordinate offsets updated. Meaning G43, G43.1, and G49
//                            // all are explicit axis commands, regardless if they require axis words or not.
//                            if (axis_command) { FAIL(STATUS_GCODE_AXIS_COMMAND_CONFLICT); } // [Axis word/command conflict] }
//                            axis_command = AXIS_COMMAND_TOOL_LENGTH_OFFSET;
//                            if (int_value == 49) { // G49
//                                gc_block.modal.tool_length = TOOL_LENGTH_OFFSET_CANCEL;
//                            } else if (mantissa == 10) { // G43.1
//                                gc_block.modal.tool_length = TOOL_LENGTH_OFFSET_ENABLE_DYNAMIC;
//                            } else { FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); } // [Unsupported G43.x command]
//                            mantissa = 0; // Set to zero to indicate valid non-integer G command.
//                            break;
//                        case 54: case 55: case 56: case 57: case 58: case 59:
//                            // NOTE: G59.x are not supported. (But their int_values would be 60, 61, and 62.)
//                            word_bit = MODAL_GROUP_G12;
//                            gc_block.modal.coord_select = int_value-54; // Shift to array indexing.
//                            break;
//                        case 61:
//                            word_bit = MODAL_GROUP_G13;
//                            if (mantissa != 0) { FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); } // [G61.1 not supported]
//                            // gc_block.modal.control = CONTROL_MODE_EXACT_PATH; // G61
//                            break;
//                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND); // [Unsupported G command]
//                    }
//                    if (mantissa > 0) { FAIL(STATUS_GCODE_COMMAND_VALUE_NOT_INTEGER); } // [Unsupported or invalid Gxx.x command]
//                    // Check for more than one command per modal group violations in the current block
//                    // NOTE: Variable 'word_bit' is always assigned, if the command is valid.
//                    if ( bit_istrue(command_words,bit(word_bit)) ) { FAIL(STATUS_GCODE_MODAL_GROUP_VIOLATION); }
//                    command_words |= bit(word_bit);
//                    break;
//
//
//                default:
//                    switch(letter){
//                        // case 'A': // Not supported
//                        // case 'B': // Not supported
//                        // case 'C': // Not supported
//                        // case 'D': // Not supported
//                        case 'F': word_bit = WORD_F; gc_block.values.f = value; break;
//                        // case 'H': // Not supported
//                        case 'I': word_bit = WORD_I; gc_block.values.ijk[X_AXIS] = value; ijk_words |= (1<<X_AXIS); break;
//                        case 'J': word_bit = WORD_J; gc_block.values.ijk[Y_AXIS] = value; ijk_words |= (1<<Y_AXIS); break;
//                        case 'K': word_bit = WORD_K; gc_block.values.ijk[Z_AXIS] = value; ijk_words |= (1<<Z_AXIS); break;
//                        case 'L': word_bit = WORD_L; gc_block.values.l = int_value; break;
//                        case 'N': word_bit = WORD_N; gc_block.values.n = (int32_t)(value); break;
//                        case 'P': word_bit = WORD_P; gc_block.values.p = value; break;
//                        // NOTE: For certain commands, P value must be an integer, but none of these commands are supported.
//                        // case 'Q': // Not supported
//                        case 'R': word_bit = WORD_R; gc_block.values.r = value; break;
//                        case 'S': word_bit = WORD_S; gc_block.values.s = value; break;
//                        case 'T': word_bit = WORD_T; break; // gc.values.t = int_value;
//                        case 'X': word_bit = WORD_X; gc_block.values.xyz[X_AXIS] = value; axis_words |= (1<<X_AXIS); break;
//                        case 'Y': word_bit = WORD_Y; gc_block.values.xyz[Y_AXIS] = value; axis_words |= (1<<Y_AXIS); break;
//                        case 'Z': word_bit = WORD_Z; gc_block.values.xyz[Z_AXIS] = value; axis_words |= (1<<Z_AXIS); break;
//                        default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND);
//                    }
//
//                    // NOTE: Variable 'word_bit' is always assigned, if the non-command letter is valid.
//                    if (bit_istrue(value_words,bit(word_bit))) { FAIL(STATUS_GCODE_WORD_REPEATED); } // [Word repeated]
//                    // Check for invalid negative values for words F, N, P, T, and S.
//                    // NOTE: Negative value check is done here simply for code-efficiency.
//                    if ( bit(word_bit) & (bit(WORD_F)|bit(WORD_N)|bit(WORD_P)|bit(WORD_T)|bit(WORD_S)) ) {
//                        if (value < 0.0) { FAIL(STATUS_NEGATIVE_VALUE); } // [Word value cannot be negative]
//                    }
//                    value_words |= bit(word_bit); // Flag to indicate parameter assigned.
//
//            }
//        }
//
//        mc_line(gc_block.values.xyz, -1.0, false, gc_state.line_number);

    }

    public static boolean readNumber(char line[], int counter, float float_ptr) {
//        char ptr = counter;
        char c = line[counter];
        boolean isnegative;
        int intval = 0;
        int exp = 0;
        int ndigit = 0;
        boolean isdecimal = false;
        float fval;

        // Grab first character and increment pointer. No spaces assumed in line.
//        c = ptr++;

        // Capture initial positive/minus character
        isnegative = false;
        if (c == '-') {
            isnegative = true;
//            c = ptr++;
        } else if (c == '+') {
//            c = ptr++;
        }

        // Extract number into fast integer. Track decimal in terms of exponent value.
        intval = 0;
        exp = 0;
        ndigit = 0;
        isdecimal = false;
        while(true) {
            c -= '0';
            if (c <= 9) {
                ndigit++;
                if (ndigit <= 8) {
                    if (isdecimal) { exp--; }
                    intval = (((intval << 2) + intval) << 1) + c; // intval*10 + c
                } else {
                    if (!(isdecimal)) { exp++; }  // Drop overflow digits
                }
            } else if (c == (('.'-'0') & 0xff)  &&  !(isdecimal)) {
                isdecimal = true;
            } else {
                break;
            }
//            c = ptr++;
        }

        // Return if no digits have been read.
//        if (!ndigit) { return(false); };

        // Convert integer into floating point.

        fval = (float)intval;

        // Apply decimal. Should perform no more than two floating point multiplications for the
        // expected range of E0 to E-4.
        if (fval != 0) {
            while (exp <= -2) {
                fval *= 0.01;
                exp += 2;
            }
            if (exp < 0) {
                fval *= 0.1;
            } else if (exp > 0) {
                do {
                    fval *= 10.0;
                } while (--exp > 0);
            }
        }

        // Assign floating point value with correct sign.
        if (isnegative) {
            float_ptr = -fval;
        } else {
            float_ptr = fval;
        }
        System.out.println(float_ptr);

//        counter = (char) (ptr - 1); // Set char_counter to next statement

        return true;
    }

    public static void ijk() {

//        float x = gc_block.values.xyz[axis_0]-gc_state.position[axis_0]; // Delta x between current position and target
//        float y = gc_block.values.xyz[axis_1]-gc_state.position[axis_1]; // Delta y between current position and target
        float x = 0 - 5; // Delta x between current position and target
        float y = 5 - 0; // Delta y between current position and target
        float r = 5;
        double i;
        double j;

//        if (value_words & bit(WORD_R)) { // Arc Radius Mode

            /*  We need to calculate the center of the circle that has the designated radius and passes through both the current position and the target position.
                我们需要计算具有指定半径且经过当前位置和目标位置的圆心。
                计算出A点到B点的半径，获取圆心坐标
                This method calculates the following set of equations where [x,y] is the vector from current to target position,
                d == magnitude of that vector,
                h == hypotenuse of the triangle formed by the radius of the circle,
                the distance to the center of the travel vector. A vector perpendicular to the travel vector [-y,x] is scaled to the
                length of h [-y/d*h, x/d*h] and added to the center of the travel vector [x/2,y/2] to form the new point
                [i,j] at [x/2-y/d*h, y/2+x/d*h] which will be the center of our arc.

                d^2 == x^2 + y^2
                h^2 == r^2 - (d/2)^2
                i == x/2 - y/d*h
                j == y/2 + x/d*h

                                                                     O <- [i,j]
                                                                  -  |
                                                        r      -     |
                                                            -        |
                                                         -           | h
                                                      -              |
                                        [0,0] ->  C -----------------+--------------- T  <- [x,y]
                                                  | <------ d/2 ---->|

                C - Current position
                T - Target position
                O - center of circle that pass through both C and T
                d - distance from C to T
                r - designated radius
                h - distance from center of CT to O

                Expanding the equations:

                d -> sqrt(x^2 + y^2)
                h -> sqrt(4 * r^2 - x^2 - y^2)/2
                i -> (x - (y * sqrt(4 * r^2 - x^2 - y^2)) / sqrt(x^2 + y^2)) / 2
                j -> (y + (x * sqrt(4 * r^2 - x^2 - y^2)) / sqrt(x^2 + y^2)) / 2

                Which can be written:

                i -> (x - (y * sqrt(4 * r^2 - x^2 - y^2))/sqrt(x^2 + y^2))/2
                j -> (y + (x * sqrt(4 * r^2 - x^2 - y^2))/sqrt(x^2 + y^2))/2

                Which we for size and speed reasons optimize to:

                h_x2_div_d = sqrt(4 * r^2 - x^2 - y^2)/sqrt(x^2 + y^2)
                i = (x - (y * h_x2_div_d))/2
                j = (y + (x * h_x2_div_d))/2
            */

            // First, use h_x2_div_d to compute 4*h^2 to check if it is negative or r is smaller
            // than d. If so, the sqrt of a negative number is complex and error out.
            double h_x2_div_d = 4.0 * r * r - x*x - y*y;

            // Finish computing h_x2_div_d.
            h_x2_div_d = - Math.sqrt(h_x2_div_d)/(Math.sqrt(x*x + y*y)); // == -(h * 2 / d)
            // Invert the sign of h_x2_div_d if the circle is counter clockwise (see sketch below)

            /* The counter clockwise circle lies to the left of the target direction. When offset is positive,
               the left hand circle will be generated - when it is negative the right hand circle is generated.

                                                                   T  <-- Target position

                                                                   ^
                        Clockwise circles with this center         |          Clockwise circles with this center will have
                        will have > 180 deg of angular travel      |          < 180 deg of angular travel, which is a good thing!
                                                         \         |          /
            center of arc when h_x2_div_d is positive ->  x <----- | -----> x <- center of arc when h_x2_div_d is negative
                                                                   |
                                                                   |

                                                                   C  <-- Current position
            */
            // Negative R is g-code-alese for "I want a circle with more than 180 degrees of travel" (go figure!),
            // even though it is advised against ever generating such circles in a single line of g-code. By
            // inverting the sign of h_x2_div_d the center of the circles is placed on the opposite side of the line of
            // travel and thus we get the unadvisably long arcs as prescribed.
//            if (gc_block.values.r < 0) {
//                h_x2_div_d = -h_x2_div_d;
//                gc_block.values.r = -gc_block.values.r; // Finished with r. Set to positive for mc_arc
//            }
            // Complete the operation by calculating the actual center of the arc
            i = 0.5*(x-(y*h_x2_div_d));
            j = 0.5*(y+(x*h_x2_div_d));

        System.out.println("i is: " + i);
        System.out.println("j is: " + j);

//        } else { // Arc Center Format Offset Mode
//            if (!(ijk_words & (bit(axis_0)|bit(axis_1)))) { FAIL(STATUS_GCODE_NO_OFFSETS_IN_PLANE); } // [No offsets in plane]
//            bit_false(value_words,(bit(WORD_I)|bit(WORD_J)|bit(WORD_K)));
//
//            // Convert IJK values to proper units.
//            if (gc_block.modal.units == UNITS_MODE_INCHES) {
//                for (idx=0; idx<N_AXIS; idx++) { // Axes indices are consistent, so loop may be used to save flash space.
//                    if (ijk_words & bit(idx)) { gc_block.values.ijk[idx] *= MM_PER_INCH; }
//                }
//            }
//
//            // Arc radius from center to target
//            x -= gc_block.values.ijk[axis_0]; // Delta x between circle center and target
//            y -= gc_block.values.ijk[axis_1]; // Delta y between circle center and target
//            target_r = hypot_f(x,y);
//
//            // Compute arc radius for mc_arc. Defined from current location to center.
//            gc_block.values.r = hypot_f(gc_block.values.ijk[axis_0], gc_block.values.ijk[axis_1]);
//
//            // Compute difference between current location and target radii for final error-checks.
//            delta_r = fabs(target_r-gc_block.values.r);
//            if (delta_r > 0.005) {
//                if (delta_r > 0.5) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Arc definition error] > 0.5mm
//                if (delta_r > (0.001*gc_block.values.r)) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Arc definition error] > 0.005mm AND 0.1% radius
//            }
//        }
    }

    public static void planner(float[] target, float feed_rate, int invert_feed_rate) {


            int target_steps[] = new int[3];
            float unit_vec[] = new float[3], delta_mm;
            byte idx;
            float inverse_unit_vec_value;
            float inverse_millimeters;  // Inverse millimeters to remove multiple float divides
            float junction_cos_theta;
            float sin_theta_d2;

            PlanBuffer block = new PlanBuffer();
            block.setStep_event_count(0);
            block.setMillimeters(0);
            block.setDirection_bits(0);
            block.setAcceleration(1.0E+38);



            for (idx=0; idx<3; idx++) {
                target_steps[idx] = (target[idx] * 250.0)>0
                                ? (int)(target[idx] * 250.0 + 0.5)
                                : (int)(target[idx] * 250.0 - 0.5);

                int[] steps = block.getSteps();
                steps[idx] = target_steps[idx];
                block.setStep_event_count(Math.max(block.getStep_event_count(), steps[idx]));
                delta_mm = (float) ((target_steps[idx])/250.0);

                unit_vec[idx] = delta_mm; // Store unit vector numerator. Denominator computed later.


                block.setMillimeters(block.getMillimeters() + delta_mm * delta_mm);
            }

            block.setMillimeters((float) Math.sqrt(block.getMillimeters()));

            // Bail if this is a zero-length block. Highly unlikely to occur.
            if (block.getStep_event_count() == 0) { return; }

            // Adjust feed_rate value to mm/min depending on type of rate input (normal, inverse time, or rapids)
            // TODO: Need to distinguish a rapids vs feed move for overrides. Some flag of some sort.
//            if (feed_rate < 0) { feed_rate = (float) 1.0E+38; } // Scaled down to absolute max/rapids rate later
//            else if (invert_feed_rate) {(int) feed_rate *= block.getMillimeters(); }
//            if (feed_rate < MINIMUM_FEED_RATE) { feed_rate = MINIMUM_FEED_RATE; } // Prevents step generation round-off condition.

            // Calculate the unit vector of the line move and the block maximum feed rate and acceleration scaled
            // down such that no individual axes maximum values are exceeded with respect to the line direction.
            // NOTE: This calculation assumes all axes are orthogonal (Cartesian) and works with ABC-axes,
            // if they are also orthogonal/independent. Operates on the absolute value of the unit vector.
            inverse_millimeters = (float) (1.0/block.getMillimeters());  // Inverse millimeters to remove multiple float divides
            junction_cos_theta = 0;
            for (idx=0; idx<3; idx++) {
                if (unit_vec[idx] != 0) {  // Avoid divide by zero.
                    unit_vec[idx] *= inverse_millimeters;  // Complete unit vector calculation
                    inverse_unit_vec_value = (float) (1.0/unit_vec[idx]); // Inverse to remove multiple float divides.

                    // Check and limit feed rate against max individual axis velocities and accelerations
                    feed_rate = Math.min(feed_rate,500*inverse_unit_vec_value);
                    block.setAcceleration(
                            Math.min(block.getAcceleration(),10.0*60*60*inverse_unit_vec_value)
                    );

                    // Incrementally compute cosine of angle between previous and current path. Cos(theta) of the junction
                    // between the current move and the previous move is simply the dot product of the two unit vectors,
                    // where prev_unit_vec is negative. Used later to compute maximum junction speed.
//                    junction_cos_theta -= pl.previous_unit_vec[idx] * unit_vec[idx];
                }
            }


//                if (junction_cos_theta > 0.999999) {
//                    //  For a 0 degree acute junction, just set minimum junction speed.
//                    block->max_junction_speed_sqr = MINIMUM_JUNCTION_SPEED*MINIMUM_JUNCTION_SPEED;
//                } else {
//                    junction_cos_theta = max(junction_cos_theta,-0.999999); // Check for numerical round-off to avoid divide by zero.
//                    sin_theta_d2 = sqrt(0.5*(1.0-junction_cos_theta)); // Trig half angle identity. Always positive.
//
//                    // TODO: Technically, the acceleration used in calculation needs to be limited by the minimum of the
//                    // two junctions. However, this shouldn't be a significant problem except in extreme circumstances.
//                    block->max_junction_speed_sqr = max( MINIMUM_JUNCTION_SPEED*MINIMUM_JUNCTION_SPEED,
//                            (block->acceleration * settings.junction_deviation * sin_theta_d2)/(1.0-sin_theta_d2) );
//
//                }
//
//            // Store block nominal speed
//            block->nominal_speed_sqr = feed_rate*feed_rate; // (mm/min). Always > 0
//
//            // Compute the junction maximum entry based on the minimum of the junction speed and neighboring nominal speeds.
//            block->max_entry_speed_sqr = min(block->max_junction_speed_sqr,
//                    min(block->nominal_speed_sqr,pl.previous_nominal_speed_sqr));

    }

    public static void main(String[] args) {
//        char line[] = {'G','1',' ','x','-','9','9','2','.','5',' ','y','2','9','.','9',' ','F','2','0','0'};
//
//        readNumber(line, 4, 0.0f);
//        ijk();
        float xyz[] = new float[3];
        xyz[0] = 12.3f;
        xyz[1] = 15.3f;
        xyz[2] = 0.0f;

        planner(xyz, 1200, 1);

    }
}
