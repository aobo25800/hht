package com.zjz.fileTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author com.zjz
 * @date 2021/9/2 11:40
 */
public class FileTest {
    public static void main(String[] args) {
        calculateSModelLine(10, 560, 160, 4);
    }

    public static void calculateSModelLine(float  len, float fre_max, float fre_min, float flexible)
    {
        List<Double> fre = new ArrayList<>();
        List<Integer> period = new ArrayList<>();
        double deno ;
        float melo;
        float delt = fre_max-fre_min;
        for(int i = 0; i<len; i++) {
            melo = flexible * (i-len/2) / (len/2);
            deno = 1.0 / (1 + Math.exp(-melo));

            double zjz = delt * deno + fre_min;

            fre.add(i, zjz);

            int zjz1 =  new Double(100000.0 / fre.get(i)).intValue();

            period.add(i, zjz1);
//            for (int j = 0; j < zjz1; j ++) {
//                System.out.print("* ");
//            }
//            System.out.println();
        }
        System.out.println("fre is: ");
        System.out.println(fre);
        System.out.println(fre.size());
        System.out.println("period is: ");
        System.out.println(period);
        System.out.println(period.size());
    }
}
