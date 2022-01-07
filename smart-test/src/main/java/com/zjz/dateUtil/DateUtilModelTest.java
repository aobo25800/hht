package com.zjz.dateUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/8/10 9:53
 */
public class DateUtilModelTest {
    public static void main(String[] args) {
        String time = "2016年09月09日";
        Date parse = DateUtil.parse(time);
//        String format = DateUtil.format(parse, "yyyy-MM-dd");
        System.out.println(parse);
    }
}
