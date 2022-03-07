package com.zjz.localDateTest;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/8/30 14:49
 */
public class LocalDateTest {
    public static void main(String[] args) {
//        LocalDate localDate = LocalDate.now().plusDays(3);
//        System.out.println(localDate.getDayOfMonth());
//        System.out.println();
//        LocalDate now = LocalDate.now();
//        System.out.println(DateUtil.format(new Date(), "yyyyMMdd"));
//        String format = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
//        System.out.println(format);

//        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
//        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
//        System.out.println(seconds);

//        DateTime dateTime = DateUtil.parse("2022-03-03 00:00:02");
//        String format = DateUtil.format(dateTime, DatePattern.NORM_MONTH_PATTERN);
//        System.out.println(dateTime);
//        System.out.println("to string: " + format);

//        String a = "1234567890";
//        System.out.println(a.substring(a.length() - 4));
        String date = "2022-03";
        DateTime parse = DateUtil.parse(date, DatePattern.NORM_MONTH_PATTERN);
        String format = DateUtil.format(parse, DatePattern.NORM_MONTH_PATTERN);
        System.out.println(format);
    }
}
