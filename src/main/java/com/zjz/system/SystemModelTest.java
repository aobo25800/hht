package com.zjz.system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author com.zjz
 * @date 2021/7/22 12:04
 */
public class SystemModelTest {
    public static void main(String[] args) {
        // 时间戳
        System.out.println(System.currentTimeMillis());
        // 默认时间戳的长度
        System.out.println("时间戳的长度：\t" + String.valueOf(System.currentTimeMillis()).length());
        // 获取系统
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperties());

        System.out.println("当前时间" + LocalDate.now());
        System.out.println("当前时间" + LocalDateTime.now());

        LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        System.out.println("localDateTime: " + localDateTime);
        try {
            Thread.sleep(1000 *10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("now: " + LocalDateTime.now());
        if (localDateTime.isBefore(LocalDateTime.now())) {
            System.out.println("hello");
        }
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