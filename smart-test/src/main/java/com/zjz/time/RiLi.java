package com.zjz.time;

import java.util.Calendar;

/**
 * @author zjz
 * @date 2022/7/26 15:34
 */
public class RiLi {
    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        System.out.println(instance);
        System.out.println(instance.getTime());

        instance.add(Calendar.MONTH, 1);
        System.out.println(instance);
        System.out.println(instance.getTime());
    }
}
