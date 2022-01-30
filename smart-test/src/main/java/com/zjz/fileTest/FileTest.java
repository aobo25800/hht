package com.zjz.fileTest;

import cn.hutool.core.date.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author com.com.zjz
 * @date 2021/9/2 11:40
 */
public class FileTest {


    public static void main(String[] args) throws IOException {
        String fileName = DateUtil.format(new Date(), "yyyyMMdd") + "学校端导出订单信息.xlsx";
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        System.out.println(file.getAbsolutePath());
    }

}

//C:\Users\DAZHI\AppData\Local\Temp\20220128学校端导出订单信息.xlsx