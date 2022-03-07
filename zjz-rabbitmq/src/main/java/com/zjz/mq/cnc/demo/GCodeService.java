package com.zjz.mq.cnc.demo;

/**
 * @author zjz
 * @date 2022/1/30 20:56
 */
public class GCodeService {

    /**
     * g代码解析
     */
    public static void codeParse(char[] line) {
        int charCounter = 0;
        // 解析行中所有的字符
        while (line[charCounter] != 0) {
            char letter = line[charCounter];

            // 如果是命令字符G, M获取命令字符后的数字
            short commandValue = parseNumber();

            charCounter ++;
            switch(letter) {
                case 'G':
//                    switch(commandValue) {
//                        case 0:
//                            gc_block.modal.motion = MOTION_MODE_SEEK; break; // G0
//                        case 1:
//                            gc_block.modal.motion = MOTION_MODE_LINEAR; break; // G1
//                        case 2:
//                            gc_block.modal.motion = MOTION_MODE_CW_ARC; break; // G2
//                        case 3:
//                            gc_block.modal.motion = MOTION_MODE_CCW_ARC; break; // G3
//                        case 54: case 55: case 56: case 57: case 58: case 59:
//                            gc_block.modal.coord_select = int_value - 54;
//                            break;
//                        default:
//                            System.out.println("**********命令值解析错误**********");
//                    }
                    break;
            default:
//                switch(letter){
//                    case 'F': word_bit = WORD_F; gc_block.values.f = value; break;
//                    // case 'H': // Not supported
//                    case 'I': word_bit = WORD_I; gc_block.values.ijk[X_AXIS] = value; ijk_words |= (1<<X_AXIS); break;
//                    case 'J': word_bit = WORD_J; gc_block.values.ijk[Y_AXIS] = value; ijk_words |= (1<<Y_AXIS); break;
//                    case 'K': word_bit = WORD_K; gc_block.values.ijk[Z_AXIS] = value; ijk_words |= (1<<Z_AXIS); break;
//                    case 'L': word_bit = WORD_L; gc_block.values.l = int_value; break;
//                    case 'N': word_bit = WORD_N; gc_block.values.n = (int32_t)(value); break;
//                    case 'P': word_bit = WORD_P; gc_block.values.p = value; break;
//                    // NOTE: For certain commands, P value must be an integer, but none of these commands are supported.
//                    // case 'Q': // Not supported
//                    case 'R': word_bit = WORD_R; gc_block.values.r = value; break;
//                    case 'S': word_bit = WORD_S; gc_block.values.s = value; break;
//                    case 'T': word_bit = WORD_T; break; // gc.values.t = int_value;
//                    case 'X': word_bit = WORD_X; gc_block.values.xyz[X_AXIS] = value; axis_words |= (1<<X_AXIS); break;
//                    case 'Y': word_bit = WORD_Y; gc_block.values.xyz[Y_AXIS] = value; axis_words |= (1<<Y_AXIS); break;
//                    case 'Z': word_bit = WORD_Z; gc_block.values.xyz[Z_AXIS] = value; axis_words |= (1<<Z_AXIS); break;
//                    default: FAIL(STATUS_GCODE_UNSUPPORTED_COMMAND);
//                }
            }
        }
    }

    public static short parseNumber() {
        return 10;
    }
}
