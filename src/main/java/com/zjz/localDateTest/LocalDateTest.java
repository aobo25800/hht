package com.zjz.localDateTest;

import java.time.LocalDate;

/**
 * @author com.zjz
 * @date 2021/8/30 14:49
 */
public class LocalDateTest {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now().minusDays(1);
        System.out.println(localDate.toString());
    }
}
