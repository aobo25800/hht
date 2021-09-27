package com.zjz.localDate;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author com.zjz
 * @date 2021/8/5 14:22
 */
public class LocalDatePackageTest {
    public static void main(String[] args) {
//
//        LocalDate start = LocalDate.parse("2018-08-28");
//        LocalDate end = LocalDate.parse("2018-09-05");
//        long year = start.until(end, ChronoUnit.YEARS);
//        long month = start.until(end, ChronoUnit.MONTHS);
//        long days = start.until(end, ChronoUnit.DAYS);
//        System.out.println("间隔：" + year + "年");
//        System.out.println("间隔：" + month + "月");
//        System.out.println("间隔：" + days + "天");
//
//        AtomicInteger atomicInteger = new AtomicInteger();
//        System.out.println(atomicInteger.addAndGet(1));

        Date date = new Date(121, 9, 29);
        System.out.println(date);
        Date now = new Date();
        System.out.println(now);



        int compare = DateUtil.compare(date, now, "yyyy-MM");
        System.out.println(compare);
    }
}
