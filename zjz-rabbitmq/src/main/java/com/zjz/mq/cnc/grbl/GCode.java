package com.zjz.mq.cnc.grbl;

/**
 * @author zjz
 * @date 2022/8/5 11:44
 */
public class GCode {

    public static void readFloat(String line, int counter) {
        byte[] bytes = line.getBytes();
        boolean isNegative = false;
        int intVal = 0;
        int exp = 0;
        int digit = 0;
        boolean isDecimal = false;
        float float_ptr;

        char c = (char) bytes[counter];
        if (c == '-') {
            isNegative = true;
            counter ++;
            c = (char) bytes[counter];
        } else if (c == '+') {
            counter ++;
            c = (char) bytes[counter];
        }

        while(true) {
            int i = (c - '0') & 0xff;
            if (i <= 9) {
                digit++;
                if (digit <= 8) {
                    if (isDecimal) { exp--; }
                    intVal = (((intVal << 2) + intVal) << 1) + i; // intVal*10 + c
                } else {
                    if (!(isDecimal)) { exp++; }  // Drop overflow digits
                }
            } else if (i == (('.' - '0') & 0xff)  &&  !(isDecimal)) {
                isDecimal = true;
            } else {
                break;
            }
            counter ++;
            if (counter >= bytes.length) {
                break;
            }
            c = (char) bytes[counter];
        }

        float fVal;
        fVal = (float) intVal;

        // Apply decimal. Should perform no more than two floating point multiplications for the
        // expected range of E0 to E-4.
        if (fVal != 0) {
            while (exp <= -2) {
                fVal *= 0.01;
                exp += 2;
            }
            if (exp < 0) {
                fVal *= 0.1;
            } else if (exp > 0) {
                do {
                    fVal *= 10.0;
                } while (--exp > 0);
            }
        }

        if (isNegative) {
            float_ptr = -fVal;
        } else {
            float_ptr = fVal;
        }
        System.out.println(float_ptr);
    }

    public static void main(String[] args) {
        String str = "-5193.0011";
        readFloat(str, 0);
    }


}
