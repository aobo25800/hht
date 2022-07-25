package com.zjz.time;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author com.com.zjz
 * @date 2021/8/10 9:53
 */
public class DateUtilModelTest {
    public static void main(String[] args) {
//        String time = "2016年09月09日";
//        Date parse = DateUtil.parse(time);
//        String format = DateUtil.format(parse, "yyyy-MM-dd");
//        System.out.println(parse);
//        Date date = new Date();
//        System.out.println(date);
//        Date date = new Date();
//        DateTime endDate = DateUtil.offsetHour(date, -24);
//        DateTime beginDate = DateUtil.offsetMinute(endDate, -10);
//        System.out.println(endDate);
//        System.out.println(beginDate);
//        long between = DateUtil.betweenMonth(DateUtil.parse("2022-05-30"), DateUtil.parse("2022-06-28"), false);
//        System.out.println("***************" + between);
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
//        Integer[] integers = set.toArray(new Integer[0]);
//        Stream.of(integers).forEach(System.out::println);
        Object[] objects = set.toArray();
        Stream.of(objects).forEach(System.out::println);
    }
}
