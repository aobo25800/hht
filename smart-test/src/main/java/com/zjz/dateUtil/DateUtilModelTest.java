package com.zjz.dateUtil;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/8/10 9:53
 */
public class DateUtilModelTest {
    public static void main(String[] args) {
        Date date = new Date(2021,10, 25);
        Date date1 = new Date(2999, 1, 1);

        int compare = DateUtil.compare(date1, date);
        System.out.println(compare);


    }
}
