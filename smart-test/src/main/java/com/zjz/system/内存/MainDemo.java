package com.zjz.system.内存;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjz
 * @date 2021/11/22 18:08
 */
public class MainDemo {
        static String  base = "string";

        public static void main(String[] args) {
//            List<String> list = new ArrayList<String>();
//            for (int i=0;i< Integer.MAX_VALUE;i++){
//                String str = base + base;
//                base = str;
//                list.add(str.intern());
//            }
            LocalDate now = LocalDate.now();
            LocalDate localDate = now.plusDays(36);
            LocalDate with = localDate.with(TemporalAdjusters.lastDayOfMonth());

            System.out.println(now);
            System.out.println(now.getMonthValue());
            System.out.println(localDate.getMonthValue());
            System.out.println(with);
        }
}
