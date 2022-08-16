package com.zjz.mq.cnc.grbl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjz
 * @date 2022/8/12 15:36
 */
public class Bresenham {

    public static void drawLine(int x0,int y0,int x1,int y1) {
        List<List<Integer>> arr = new ArrayList<>();
        int x = x0;
        int y = y0;

        int w = x1 - x0;
        int h = y1 - y0;

        int dx1 = w < 0 ? -1: (w > 0 ? 1 : 0);
        int dy1 = h < 0 ? -1: (h > 0 ? 1 : 0);

        int dx2 = w < 0 ? -1: (w > 0 ? 1 : 0);
        int dy2 = 0;

        int fastStep = Math.abs(w);
        int slowStep = Math.abs(h);
        if (fastStep <=slowStep) {
            fastStep= Math.abs(h);
            slowStep= Math.abs(w);

            dx2= 0;
            dy2= h < 0 ? -1 : (h > 0 ? 1 : 0);
        }
        int numerator = fastStep>> 1;

        for (int i = 0; i <=fastStep; i++) {
            List<Integer> point = new ArrayList<>();

            numerator += slowStep;
            if (numerator >=fastStep) {
                numerator-= fastStep;
                point.add(x += dx1);
                point.add(y += dy1);
            }else {
                point.add(x += dx2);
                point.add(y += dy2);
            }
            arr.add(point);
        }
        System.out.println(arr);
    }

    public static void main(String[] args) {
        drawLine(0, 0, 10, 3);
    }

}
