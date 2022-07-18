package com.zjz.enums;

/**
 * @author com.com.zjz
 * @date 2021/9/24 15:46
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 枚举对象是懒加载的，调用时才会加载
         */
        Thread.sleep(5000L);
        String name = ReceiveAccountType.NAME.getDesc();
        System.out.println("name is: " + name);

        System.out.println(ReceiveAccountType.valueOf("NAME"));

        String valueOf = ReceiveAccountType.getValueOf(2);
        System.out.println(valueOf);
    }
}
