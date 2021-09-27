package com.zjz;

import java.math.BigDecimal;

/**
 * @author com.zjz
 * @date 2021/8/19 15:52
 */
public class bigDecimalTest {

    public static void main(String[] args) {
//        BigDecimal bd = new BigDecimal("17.01");
//        String textBD = bd.toPlainString();
//        System.out.println("length = "+textBD.length());
//        int radixLoc = textBD.indexOf('.');
//        System.out.println("Fraction "+textBD.substring(0,radixLoc)+"Cents: " + textBD.substring(radixLoc + 1,textBD.length()));

//        BigDecimal bd = new BigDecimal("42.99");
//
//        BigDecimal integerPart = bd.setScale(0, RoundingMode.DOWN);
//        BigDecimal fractionalPart = bd.subtract(integerPart);
//
//        System.out.println(integerPart);
//        System.out.println(fractionalPart);

        // 金额对比
//        BigDecimal notRepaidSum = new BigDecimal("-2");
//        System.out.println(notRepaidSum.compareTo(new BigDecimal("0")));

        // 金额向上取整
//        BigDecimal m = new BigDecimal("10.99");
//        System.out.print(m.setScale(0, RoundingMode.CEILING));

        // 金额对比

        System.out.println(new BigDecimal("10.99").compareTo(new BigDecimal("12.99")));     // 前者大于后者，返回值为：1  两者相等，返回值为：0 前者小于后者，返回值为：-1

        // 金额转字符串
//        BigDecimal c = new BigDecimal("12.12");
//        System.out.println(c.toString());


    }
}
