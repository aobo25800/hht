package com.zjz;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author com.com.zjz
 * @date 2021/8/19 15:52
 */
public class bigDecimalTest {

    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("0");
        System.out.println(bigDecimal);
    }
    @Data
    public static class Zjz {
        private String name;
        private String name1;

    }
}
