package com.zjz.file.controller;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author zjz
 * @date 2022/3/14 15:15
 */
@RestController
public class UploadController {

    @PostMapping("upload")
    @ResponseBody
    public void upload(@RequestParam("file") MultipartFile file) throws IOException {
        String name = file.getName();
        long size = file.getSize();
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 获得 classpath 的绝对路径
        String filePath = "/root/" + fileName;
        File newFile = new File(filePath);
        // 如果文件夹不存在、则新建
//        if (!newFile.exists()) newFile.mkdirs();

        System.out.println("start time" + LocalDateTime.now());
        // 上传
        file.transferTo(newFile);
        System.out.println("end time" + LocalDateTime.now());

    }

}
