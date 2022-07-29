package com.zjz.mq.cnc;

import com.zjz.mq.cnc.config.ThreadPoolConfig;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zjz
 * @date 2022/3/3 15:22
 */
public class CNCApplication {

    private static final ThreadPoolExecutor threadPoolExecutor = ThreadPoolConfig.commonThreadPool();

    public static void main(String[] args) {
        threadPoolExecutor.execute(() -> func());
    }

    public static void func() {
        System.out.println("func 函数执行了。。。");
        threadPoolExecutor.execute(() -> def());
    }

    public static void def() {
        System.out.println("def 函数执行了");
    }
}
