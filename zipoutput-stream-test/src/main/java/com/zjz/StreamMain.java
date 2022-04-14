package com.zjz;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;

import java.io.*;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zjz
 * @date 2021/12/14 14:03
 */
public class StreamMain {
    public static void main(String[] args) throws IOException {
        String pdf = "0M8R4KGxG";
        List<String> strings = new ArrayList<>();
        strings.add(pdf);

        File zipFile = new File("D:/", "testName10.zip");

        try (ZipOutputStream zipOs = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (int i=0; i<2; i++) {
                if (i == 1){
                    TimeUnit.SECONDS.sleep(10);
                }
                byte[] decode = Base64.getDecoder().decode(strings.get(i));
                zipOs.putNextEntry(new ZipEntry(i + ".xls"));
                zipOs.write(decode);
                zipOs.closeEntry();
            }
        }catch (Exception e) {

        }

    }
}

