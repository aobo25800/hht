package com.zjz.objectToMap;

import cn.hutool.core.codec.Base64;

import java.io.File;

/**
 * @author com.com.zjz
 * @date 2021/8/30 18:39
 */
public class TestMain {

    public static void main(String[] args) {
//        data:application/pdf;base64,
        File file = new File("");
        String pdf = "";

        byte[] decode = Base64.decode(pdf);
        Base64.decodeToFile(pdf, file);

//        Base64.decodeToFile(pdf, file);
//        byte[] decode = Base64.decode(pdf);
//        FileUtil.writeBytes(decode, file);
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
//                10,
//                20,
//                60,
//                TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(100),
//                new ThreadFactory() {
//                    private final AtomicInteger threadNumber = new AtomicInteger(1);
//
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread t = new Thread(r);
//                        t.setName("CommonPool-" + threadNumber.getAndIncrement());
//                        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                            @Override
//                            public void uncaughtException(Thread t, Throwable e) {
//                                System.out.println("Thread: " + t + " exited with Exception:{}");
//                            }
//                        });
//                        return t;
//                    }
//                }
//        );
//
//        threadPoolExecutor.execute(
//                TestMain::func
//        );
//        System.out.println("main end");
    }

    public static void func() {
        System.out.println("线程执行了");
    }

}
