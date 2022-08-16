package com.zjz.money;

import com.zjz.constant.CommonConstant;
import com.zjz.enums.CommonEnum;
import com.zjz.service.CommonService;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author com.com.zjz
 * @date 2021/8/19 15:52
 */
public class BigDecimalTest {

    public static void main(String[] args) {

//        BigDecimal bigDecimal = new BigDecimal("0");
//        System.out.println(bigDecimal);
//
//        System.out.println(CommonConstant.CAR_NAME);
//        System.out.println(CommonEnum.people.getDesc());
//
//        CommonService commonService = new CommonService();
//        String s = commonService.commonFunc();
//        System.out.println("jar package return value: " + s);

        BigDecimal a = new BigDecimal("110");
        BigDecimal b = new BigDecimal("100.76");
        /**
         * 金额相加
         */
        BigDecimal add = a.add(b);
        System.out.println("金额相加方法add(): " + add);
        /**
         * 金额相减
         */
        BigDecimal subtract = a.subtract(b);
        System.out.println("金额相减方法subtract(): " + subtract);
        /**
         * 金额相乘
         */
        BigDecimal multiply = a.multiply(b);
        System.out.println("金额相乘方法multiply(): " + multiply);
        /**
         * 金额相除
         */
        BigDecimal divide = b.divide(a);
        System.out.println("金额相除方法remainder()： " + divide);
    }
}
