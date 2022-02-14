package com.zjz.IO;

import cn.hutool.core.date.DateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/9/2 11:40
 */
public class FileTest {


    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream("C:\\Users\\DAZHI\\Desktop\\IOtest.jpg");
        int num = 0;
        while (true) {
            int read = inputStream.read();
            if (read == -1) {
                break;
            }
            StringBuilder stringBuilder = new StringBuilder(Integer.toHexString((read & 0x000000FF) | 0xFFFFFF00).substring(6) + " ");
            System.out.println(num + ": " + read + "\t" + stringBuilder);
            num ++;
        }
        inputStream.close();
    }

    public static StringBuilder subBytesToStr(byte[] bytes, int len){
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < len; i ++){
            StringBuilder a = new  StringBuilder(Integer.toHexString((bytes[i] & 0x000000FF) | 0xFFFFFF00).substring(6) + " ");
            res.append(a);
        }
        return res;
    }

}

// C:\Users\DAZHI\Desktop\IOtest.jpg