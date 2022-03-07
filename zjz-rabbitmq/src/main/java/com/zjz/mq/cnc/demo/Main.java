package com.zjz.mq.cnc.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author zjz
 * @date 2022/1/29 18:35
 */
public class Main {
    public static void main(String[] args) {
        // 记录一行G代码
        String line;

        try {
            // 读取文件
            File gFile = new File("");
            FileReader fileReader = new FileReader(gFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                char[]  charArray = line.toCharArray();
                for (int i = 0; i < charArray.length; i++) {

                }
            }
        }catch (Exception e) {
            System.out.println("查询文件异常");
        }
    }

    public static void test(float x1, float y1, float x2, float y2, double r) {

        double h_x2_div_d;
        // Check if feed rate is defined for the motion modes that require it.
        // 进给速度为0 报错
//        if (gc_block.values.f == 0.0) { FAIL(STATUS_GCODE_UNDEFINED_FEED_RATE); } // [Feed rate undefined]

//        switch (gc_block.modal.motion) {
//            case MOTION_MODE_LINEAR:
//                // [G1 Errors]: Feed rate undefined. Axis letter not configured or without real value.
//                // Axis words are optional. If missing, set axis command flag to ignore execution.
//                // 没有配置轴 配置默认
//                if (!axis_words) { axis_command = AXIS_COMMAND_NONE; }
//                break;
//            case MOTION_MODE_CW_ARC: case MOTION_MODE_CCW_ARC:
        // [G2/3 Errors All-Modes]: Feed rate undefined.
        // [G2/3 Radius-Mode Errors]: No axis words in selected plane. Target point is same as current.
        // [G2/3 Offset-Mode Errors]: No axis words and/or offsets in selected plane. The radius to the current
        //   point and the radius to the target point differs more than 0.002mm (EMC def. 0.5mm OR 0.005mm and 0.1% radius).
        // [G2/3 Full-Circle-Mode Errors]: NOT SUPPORTED. Axis words exist. No offsets programmed. P must be an integer.
        // NOTE: Both radius and offsets are required for arc tracing and are pre-computed with the error-checking.
        // 没有配置轴 报错
//                if (!axis_words) { FAIL(STATUS_GCODE_NO_AXIS_WORDS); } // [No axis words]
//                if (!(axis_words & (bit(axis_0)|bit(axis_1)))) { FAIL(STATUS_GCODE_NO_AXIS_WORDS_IN_PLANE); } // [No axis words in plane]

        // Calculate the change in position along each selected axis

//                x = gc_block.values.xyz[axis_0] - gc_state.position[axis_0]; // Delta x between current position and target
//                y = gc_block.values.xyz[axis_1] - gc_state.position[axis_1]; // Delta y between current position and target
        float x = x1 - x2;
        float y = y1 - y2;

//        bit_false(value_words,bit(WORD_R));
//        if (gc_check_same_position(gc_state.position, gc_block.values.xyz)) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Invalid target]

        // Convert radius value to proper units.
        // 将半径转为指定的单位
//        if (gc_block.modal.units == UNITS_MODE_INCHES) { gc_block.values.r *= MM_PER_INCH; }
                    /*  We need to calculate the center of the circle that has the designated radius and passes
                        through both the current position and the target position. This method calculates the following
                        set of equations where [x,y] is the vector from current to target position, d == magnitude of
                        that vector, h == hypotenuse of the triangle formed by the radius of the circle, the distance to
                        the center of the travel vector. A vector perpendicular to the travel vector [-y,x] is scaled to the
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
//        h_x2_div_d = 4.0 * gc_block.values.r * gc_block.values.r - x*x - y*y;
        h_x2_div_d = 4.0 * r * r - x*x - y*y;

//        if (h_x2_div_d < 0) { FAIL(STATUS_GCODE_ARC_RADIUS_ERROR); } // [Arc radius error]

        // Finish computing h_x2_div_d.
        h_x2_div_d = -Math.sqrt(h_x2_div_d)/Math.sqrt(x*x + y*y); // == -(h * 2 / d)
        // Invert the sign of h_x2_div_d if the circle is counter clockwise (see sketch below)
//        if (gc_block.modal.motion == MOTION_MODE_CCW_ARC) { h_x2_div_d = -h_x2_div_d; }

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
//        if (gc_block.values.r < 0) {
//            h_x2_div_d = -h_x2_div_d;
//            gc_block.values.r = -gc_block.values.r; // Finished with r. Set to positive for mc_arc
//        }
        // Complete the operation by calculating the actual center of the arc
//        gc_block.values.ijk[axis_0] = 0.5*(x-(y*h_x2_div_d));
//        gc_block.values.ijk[axis_1] = 0.5*(y+(x*h_x2_div_d));
        double i = 0.5*(x-(y*h_x2_div_d));
        double j = 0.5*(y+(x*h_x2_div_d));
        System.out.println("i is: " + i);
        System.out.println("j is: " + j);
//
//                if (value_words & bit(WORD_R)) { // Arc Radius Mode
//                    bit_false(value_words,bit(WORD_R));
//                    if (gc_check_same_position(gc_state.position, gc_block.values.xyz)) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Invalid target]
//
//                    // Convert radius value to proper units.
//                    if (gc_block.modal.units == UNITS_MODE_INCHES) { gc_block.values.r *= MM_PER_INCH; }
//                    /*  We need to calculate the center of the circle that has the designated radius and passes
//                        through both the current position and the target position. This method calculates the following
//                        set of equations where [x,y] is the vector from current to target position, d == magnitude of
//                        that vector, h == hypotenuse of the triangle formed by the radius of the circle, the distance to
//                        the center of the travel vector. A vector perpendicular to the travel vector [-y,x] is scaled to the
//                        length of h [-y/d*h, x/d*h] and added to the center of the travel vector [x/2,y/2] to form the new point
//                        [i,j] at [x/2-y/d*h, y/2+x/d*h] which will be the center of our arc.
//
//                        d^2 == x^2 + y^2
//                        h^2 == r^2 - (d/2)^2
//                        i == x/2 - y/d*h
//                        j == y/2 + x/d*h
//
//                                                                             O <- [i,j]
//                                                                          -  |
//                                                                r      -     |
//                                                                    -        |
//                                                                 -           | h
//                                                              -              |
//                                                [0,0] ->  C -----------------+--------------- T  <- [x,y]
//                                                          | <------ d/2 ---->|
//
//                        C - Current position
//                        T - Target position
//                        O - center of circle that pass through both C and T
//                        d - distance from C to T
//                        r - designated radius
//                        h - distance from center of CT to O
//
//                        Expanding the equations:
//
//                        d -> sqrt(x^2 + y^2)
//                        h -> sqrt(4 * r^2 - x^2 - y^2)/2
//                        i -> (x - (y * sqrt(4 * r^2 - x^2 - y^2)) / sqrt(x^2 + y^2)) / 2
//                        j -> (y + (x * sqrt(4 * r^2 - x^2 - y^2)) / sqrt(x^2 + y^2)) / 2
//
//                        Which can be written:
//
//                        i -> (x - (y * sqrt(4 * r^2 - x^2 - y^2))/sqrt(x^2 + y^2))/2
//                        j -> (y + (x * sqrt(4 * r^2 - x^2 - y^2))/sqrt(x^2 + y^2))/2
//
//                        Which we for size and speed reasons optimize to:
//
//                        h_x2_div_d = sqrt(4 * r^2 - x^2 - y^2)/sqrt(x^2 + y^2)
//                        i = (x - (y * h_x2_div_d))/2
//                        j = (y + (x * h_x2_div_d))/2
//                    */
//
//                    // First, use h_x2_div_d to compute 4*h^2 to check if it is negative or r is smaller
//                    // than d. If so, the sqrt of a negative number is complex and error out.
//                    h_x2_div_d = 4.0 * gc_block.values.r*gc_block.values.r - x*x - y*y;
//
//                    if (h_x2_div_d < 0) { FAIL(STATUS_GCODE_ARC_RADIUS_ERROR); } // [Arc radius error]
//
//                    // Finish computing h_x2_div_d.
//                    h_x2_div_d = -sqrt(h_x2_div_d)/hypot_f(x,y); // == -(h * 2 / d)
//                    // Invert the sign of h_x2_div_d if the circle is counter clockwise (see sketch below)
//                    if (gc_block.modal.motion == MOTION_MODE_CCW_ARC) { h_x2_div_d = -h_x2_div_d; }
//
//                    /* The counter clockwise circle lies to the left of the target direction. When offset is positive,
//                       the left hand circle will be generated - when it is negative the right hand circle is generated.
//
//                                                                           T  <-- Target position
//
//                                                                           ^
//                                Clockwise circles with this center         |          Clockwise circles with this center will have
//                                will have > 180 deg of angular travel      |          < 180 deg of angular travel, which is a good thing!
//                                                                 \         |          /
//                    center of arc when h_x2_div_d is positive ->  x <----- | -----> x <- center of arc when h_x2_div_d is negative
//                                                                           |
//                                                                           |
//
//                                                                           C  <-- Current position
//                    */
//                    // Negative R is g-code-alese for "I want a circle with more than 180 degrees of travel" (go figure!),
//                    // even though it is advised against ever generating such circles in a single line of g-code. By
//                    // inverting the sign of h_x2_div_d the center of the circles is placed on the opposite side of the line of
//                    // travel and thus we get the unadvisably long arcs as prescribed.
//                    if (gc_block.values.r < 0) {
//                        h_x2_div_d = -h_x2_div_d;
//                        gc_block.values.r = -gc_block.values.r; // Finished with r. Set to positive for mc_arc
//                    }
//                    // Complete the operation by calculating the actual center of the arc
//                    gc_block.values.ijk[axis_0] = 0.5*(x-(y*h_x2_div_d));
//                    gc_block.values.ijk[axis_1] = 0.5*(y+(x*h_x2_div_d));
//
//                } else { // Arc Center Format Offset Mode
//                    if (!(ijk_words & (bit(axis_0)|bit(axis_1)))) { FAIL(STATUS_GCODE_NO_OFFSETS_IN_PLANE); } // [No offsets in plane]
//                    bit_false(value_words,(bit(WORD_I)|bit(WORD_J)|bit(WORD_K)));
//
//                    // Convert IJK values to proper units.
//                    if (gc_block.modal.units == UNITS_MODE_INCHES) {
//                        for (idx=0; idx<N_AXIS; idx++) { // Axes indices are consistent, so loop may be used to save flash space.
//                            if (ijk_words & bit(idx)) { gc_block.values.ijk[idx] *= MM_PER_INCH; }
//                        }
//                    }
//
//                    // Arc radius from center to target
//                    x -= gc_block.values.ijk[axis_0]; // Delta x between circle center and target
//                    y -= gc_block.values.ijk[axis_1]; // Delta y between circle center and target
//                    target_r = hypot_f(x,y);
//
//                    // Compute arc radius for mc_arc. Defined from current location to center.
//                    gc_block.values.r = hypot_f(gc_block.values.ijk[axis_0], gc_block.values.ijk[axis_1]);
//
//                    // Compute difference between current location and target radii for final error-checks.
//                    delta_r = fabs(target_r-gc_block.values.r);
//                    if (delta_r > 0.005) {
//                        if (delta_r > 0.5) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Arc definition error] > 0.5mm
//                        if (delta_r > (0.001*gc_block.values.r)) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Arc definition error] > 0.005mm AND 0.1% radius
//                    }
//                }
//                break;
//            case MOTION_MODE_PROBE_TOWARD: case MOTION_MODE_PROBE_TOWARD_NO_ERROR:
//            case MOTION_MODE_PROBE_AWAY: case MOTION_MODE_PROBE_AWAY_NO_ERROR:
//                // [G38 Errors]: Target is same current. No axis words. Cutter compensation is enabled. Feed rate
//                //   is undefined. Probe is triggered. NOTE: Probe check moved to probe cycle. Instead of returning
//                //   an error, it issues an alarm to prevent further motion to the probe. It's also done there to
//                //   allow the planner buffer to empty and move off the probe trigger before another probing cycle.
//                if (!axis_words) { FAIL(STATUS_GCODE_NO_AXIS_WORDS); } // [No axis words]
//                if (gc_check_same_position(gc_state.position, gc_block.values.xyz)) { FAIL(STATUS_GCODE_INVALID_TARGET); } // [Invalid target]
//                break;
//        }
    }
}
