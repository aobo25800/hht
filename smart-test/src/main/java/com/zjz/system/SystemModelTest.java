package com.zjz.system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/7/22 12:04
 */
public class SystemModelTest {
    public static void main(String[] args) {
        // 时间戳
        System.out.println("系统时间戳: " + System.currentTimeMillis());
        // 默认时间戳的长度
        System.out.println("时间戳的长度：" + String.valueOf(System.currentTimeMillis()).length());
        // 获取系统
        System.out.println("获取系统: " + System.getProperty("os.name"));
        System.out.println("获取系统属性: " + System.getProperties());

        System.out.println("当前时间: " + LocalDate.now());
        System.out.println("当前时间(时分秒毫秒): " + LocalDateTime.now());

        LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        System.out.println("localDateTime: " + localDateTime);
        try {
            Thread.sleep(1000 *1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("now: " + LocalDateTime.now());
        if (localDateTime.isBefore(LocalDateTime.now())) {
            System.out.println("hello");
        }

        System.out.println("时间戳转为时间: " + new Date(System.currentTimeMillis()));

        int count = 0;
        System.out.println("开始时间: " + System.currentTimeMillis());
        for (int i = 0; i < 1000000; i ++) {
            count ++;
        }
        // 一百万次 12毫秒
        System.out.println("结束时间: " + System.currentTimeMillis());
        System.out.println("count is: " + count);
    }
}

class Student{
    private String name;
    private String year;
    private String sex;

    public Student() {}

    public Student(String name, String year, String sex) {
        this.name = name;
        this.year = year;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}