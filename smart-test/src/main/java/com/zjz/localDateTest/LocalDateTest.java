package com.zjz.localDateTest;

import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        LocalDate now = LocalDate.now();
        System.out.println(DateUtil.format(new Date(), "yyyyMMdd"));

    }
}
