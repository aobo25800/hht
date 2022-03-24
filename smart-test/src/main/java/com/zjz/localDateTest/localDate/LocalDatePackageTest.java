package com.zjz.localDateTest.localDate;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/8/5 14:22
 */
public class LocalDatePackageTest {
    public static void main(String[] args) {
        long l = 1647418785L;
        // 一天的开始
        DateTime dateTime1 = DateUtil.beginOfDay(new Date(l * 1000));
        System.out.println("beginDay is: " + DateUtil.format(dateTime1, DatePattern.NORM_DATETIME_PATTERN));
        // 延迟1秒
        DateTime dateTime2 = DateUtil.offsetSecond(dateTime1, 1);
        System.out.println("延迟1秒: " + DateUtil.format(dateTime2, DatePattern.NORM_DATETIME_PATTERN));
        // 一天的结束
        DateTime dateTime3 = DateUtil.endOfDay(new Date(l * 1000));
        System.out.println("endOfDay is: " + DateUtil.format(dateTime3, DatePattern.NORM_DATETIME_PATTERN));
        // 延迟1秒
        DateTime dateTime4 = DateUtil.offsetSecond(dateTime3, 60);
        System.out.println("延迟60秒: " + DateUtil.format(dateTime4, DatePattern.NORM_DATETIME_PATTERN));

    }
}
