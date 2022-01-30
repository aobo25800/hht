package com.zjz.demo;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zjz
 * @date 2021/11/15 15:50
 */
public class ExceptionMain {
    public static void main(String[] args) {
//        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
//        System.out.println(format);
//        System.out.println(getTokenTimeout());
//        BigDecimal bigDecimal = new BigDecimal("12");
//        BigDecimal bigDecimal1 = new BigDecimal("1.0");

//        int i = bigDecimal1.compareTo(new BigDecimal(0));
//        System.out.println(i);
//
//        BigDecimal multiply = bigDecimal.multiply(bigDecimal1);
//        System.out.println(multiply);

//        BigDecimal divide = bigDecimal1.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
//        System.out.println(divide);

    }

    public static long getTokenTimeout() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

}
