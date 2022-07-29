package com.zjz.mq.cnc.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zjz
 * @date 2022/7/29 16:54
 */
public class ThreadPoolConfig {
    public static ThreadPoolExecutor commonThreadPool() {
        return new ThreadPoolExecutor(
                10,
                20,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        System.out.println(t.getId());
                        t.setName("CommonPool-" + threadNumber.getAndIncrement());
                        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                            @Override
                            public void uncaughtException(Thread t, Throwable e) {

                            }
                        });
                        return t;
                    }
                }
        );
    }
}
