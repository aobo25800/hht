package com.zjz.danLi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author com.zjz
 * @date 2021/9/23 17:45
 */
public class Lazy {

    private Lazy() {
        System.out.println(Thread.currentThread().getName() + " loading ok");
    }

    private volatile static Lazy lazy;

    public static Lazy getInstance() {
        if(lazy == null) {
            synchronized (Lazy.class) {
                if(lazy == null) {
                    lazy = new Lazy();
                }
            }
        }
        return lazy;
    }


    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

        // 在使用volatile关键字之后，单例可能还是不安全的，因为java可以通过反射获取对象
        Lazy instance1 = Lazy.getInstance();
        Class<Lazy> lazyClass = Lazy.class;
        Constructor<Lazy> declaredConstructor = lazyClass.getDeclaredConstructor(null);
        declaredConstructor.setAccessible(true);
        Lazy instance2 = declaredConstructor.newInstance(null);
        System.out.println(instance1);
        System.out.println(instance2);

    }

}
