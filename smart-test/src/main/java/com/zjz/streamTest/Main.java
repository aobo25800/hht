package com.zjz.streamTest;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author com.com.zjz
 * @date 2021/9/10 14:50
 */
public class Main {

    public static void main(String[] args) {
        // 字符串排序
//        String[] strings = {"2022-08-05", "2022-09-05", "2022-10-05", "2022-03-05", "2022-04-05", "2022-05-05", "2022-06-05", "2022-07-05", "2022-11-05", "2022-12-05", "2023-01-05", "2023-02-05"};
//        List<String> collect = Arrays.stream(strings).sorted().collect(Collectors.toList());
//        System.out.println(collect);
//
//        List<LocalDate> localDates = new ArrayList<>();
//        // 时间排序
//        localDates.add(LocalDate.parse("2022-07-05"));
//        localDates.add(LocalDate.parse("2022-04-05"));
//        localDates.add(LocalDate.parse("2022-05-05"));
//        localDates.add(LocalDate.parse("2023-02-05"));
//        localDates.add(LocalDate.parse("2022-06-05"));
//        localDates.add(LocalDate.parse("2022-03-05"));
//        localDates.add(LocalDate.parse("2022-08-05"));
//        localDates.add(LocalDate.parse("2022-09-05"));
//        localDates.add(LocalDate.parse("2022-10-05"));
//        localDates.add(LocalDate.parse("2022-11-05"));
//        localDates.add(LocalDate.parse("2022-12-05"));
//        localDates.add(LocalDate.parse("2023-01-05"));
//        localDates.sort((t1, t2) -> t1.compareTo(t2));
//        System.out.println(localDates);
//
//        List<Date> dateList = new ArrayList<>();
//        dateList.add(DateUtil.parse("2022-04-05"));
//        dateList.add(DateUtil.parse("2022-07-05"));
//        dateList.add(DateUtil.parse("2022-05-05"));
//        dateList.add(DateUtil.parse("2022-10-05"));
//        dateList.add(DateUtil.parse("2023-01-05"));
//        dateList.add(DateUtil.parse("2022-08-05"));
//        dateList.add(DateUtil.parse("2022-06-05"));
//        dateList.add(DateUtil.parse("2022-09-05"));
//        dateList.add(DateUtil.parse("2022-11-05"));
//        dateList.add(DateUtil.parse("2022-12-05"));
//        dateList.add(DateUtil.parse("2022-03-05"));
//        dateList.add(DateUtil.parse("2023-02-05"));
//        dateList.sort((t1, t2) -> t1.compareTo(t2));
//        System.out.println(dateList);
//        Date date = new Date(1669269097000L);
//        System.out.println(date);

        LocalDate now = LocalDate.now();
        LocalDate localDate = now.minusMonths(1);
        System.out.println("now: " + now);
        System.out.println("localDate: " + localDate);

        System.out.println(now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));
        System.out.println(localDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));

        String begin = LocalDate.now()
                .format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));

    }
}
