package com.zjz.mq.cnc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author zjz
 * @date 2022/1/29 18:35
 */
public class Main {
    public static void main(String[] args) {
        // 记录一行G代码
        String line;

        try {
            // 读取文件
            File gFile = new File("");
            FileReader fileReader = new FileReader(gFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                char[]  charArray = line.toCharArray();
                for (int i = 0; i < charArray.length; i++) {

                }
            }
        }catch (Exception e) {
            System.out.println("查询文件异常");
        }
    }
}
