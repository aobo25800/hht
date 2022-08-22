package com.zjz.mq.cnc.grbl;

/**
 * @author zjz
 * @date 2022/8/5 11:44
 */
public class gcode {

    public static boolean readFloat(String line, int counter, float float_ptr) {
//        char ptr = counter;
        byte[] bytes = line.getBytes();
        char c = (char) bytes[counter];
        boolean isnegative;
        int intval = 0;
        int exp = 0;
        int ndigit = 0;
        boolean isdecimal = false;

        // Grab first character and increment pointer. No spaces assumed in line.
//        c = ptr++;

        // Capture initial positive/minus character
        isnegative = false;
        if (c == '-') {
            isnegative = true;
            counter ++;
            c = (char) bytes[counter];
        } else if (c == '+') {
            counter ++;
            c = (char) bytes[counter];
        }

        // Extract number into fast integer. Track decimal in terms of exponent value.
        while(true) {
            int i = (c - '0') & 0xff;
            if (i <= 9) {
                ndigit++;
                if (ndigit <= 8) {
                    if (isdecimal) { exp--; }
                    intval = (((intval << 2) + intval) << 1) + i; // intval*10 + c
                } else {
                    if (!(isdecimal)) { exp++; }  // Drop overflow digits
                }
            } else if (i == (('.'-'0') & 0xff)  &&  !(isdecimal)) {
                isdecimal = true;
            } else {
                break;
            }
            counter ++;
            if (counter >= bytes.length) {
                break;
            }
            c = (char) bytes[counter];
        }

        // Return if no digits have been read.
//        if (!ndigit) { return(false); };

        // Convert integer into floating point.
        float fval;
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

    public static void main(String[] args) {
        String str = "-5193.0011";
        readFloat(str, 0, 0);
    }


}
