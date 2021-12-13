package com.zjz.localDateTest;

import java.time.LocalDate;

/**
 * @author com.com.zjz
 * @date 2021/8/30 14:49
 */
public class LocalDateTest {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now().plusDays(3);
        System.out.println(localDate.getDayOfMonth());
        System.out.println();

    }
}
