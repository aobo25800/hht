package com.zjz.thread;

/**
 * @author zjz
 * @date 2022/8/8 11:43
 */
public class Main {
    public static void main(String[] args) {
        RunnableDemo runnableDemo = new RunnableDemo();
        new Thread(runnableDemo).start();
        System.out.println("hello");
    }
}
