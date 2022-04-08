package com.zjz.objectToMap;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author com.com.zjz
 * @date 2021/8/30 18:39
 */
public class TestMain {

    public static void main(String[] args) {
        String[] a = {"123", "234"};
        System.out.println(a[0]);
        List<String> list = new ArrayList<>();

        list.add("123");
        list.add("234");
        list.add("345");
        list.add("456");
        list.add("456");
        int index = list.indexOf("3455");
        System.out.println("index is: " + index);
        System.out.println(list.size());
    }

}
