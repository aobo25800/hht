package com.zjz.thread;

import lombok.SneakyThrows;

/**
 * @author zjz
 * @date 2022/8/8 11:40
 */
public class RunnableDemo implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        for (int i = 0; i < 10; i ++) {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getId());
        }
    }
}
