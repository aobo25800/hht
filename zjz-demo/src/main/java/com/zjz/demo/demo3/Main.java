package com.zjz.demo.demo3;

/**
 * @author zjz
 * @date 2021/11/17 18:16
 */
public class Main {
    public static void main(String[] args) {
        TestBuilder build = new TestBuilder.Builder()
                .age(12)
                .id(1L)
                .name("123")
                .build();
        System.out.println(build.getA());
        System.out.println(build.getAge());
    }
}
