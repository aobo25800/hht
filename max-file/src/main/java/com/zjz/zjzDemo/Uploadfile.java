package com.zjz.zjzDemo;


import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zjz
 * @date 2022/4/12 11:03
 */
@RestController
public class Uploadfile {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @CrossOrigin
    @PostMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(MultipartFileParam form, @RequestParam(value = "data", required = false) MultipartFile multipartFile ) throws IOException {
        Map<String, Object> map = null;

        try {
            long beginTime = System.currentTimeMillis();
            map = realUpload(form, multipartFile);
            System.out.println("long time: " + (System.currentTimeMillis() - beginTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @CrossOrigin
    @PostMapping("/isUpload")
    @ResponseBody
    public Map<String,Object> isUpload(MultipartFileParam param){
        return findByFileMd5(param.getMd5());
    }

    public Map<String, Object> realUpload(MultipartFileParam form, MultipartFile multipartFile) throws Exception {
        String action = form.getAction();
        String fileId = form.getUuid();
        Integer index = Integer.valueOf(form.getIndex());
        String partMd5 = form.getPartMd5();
        String md5 = form.getMd5();
        Integer total = Integer.valueOf(form.getTotal());
        String fileName = form.getName();
        String size = form.getSize();
        String suffix = NameUtil.getExtensionName(fileName);

        String saveDirectory = System.getProperty("user.dir") + File.separator + "file" + File.separator + fileId;
        String filePath = saveDirectory + File.separator + fileId + "." + suffix;
        System.out.println("**********************\t" + saveDirectory);
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片位置
        File file = new File(saveDirectory, fileId + "_" + index);
        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        Map<String, Object> map = null;
        if ("check".equals(action)){
            String md5Str = FileMd5Util.getFileMD5(file);
            if (md5Str != null && md5Str.length() == 31) {
                System.out.println("check length =" + partMd5.length() + " md5Str length" + md5Str.length() + "   " + partMd5 + " " + md5Str);
                md5Str = "0" + md5Str;
            }

            if (md5Str != null && md5Str.equals(partMd5)) {
                //分片已上传过
                map = new HashMap<>();
                map.put("flag", "1");
                map.put("fileId", fileId);
                if(!index .equals(total))
                    return map;
            } else {
                //分片未上传
                map = new HashMap<>();
                map.put("flag", "0");
                map.put("fileId", fileId);
                return map;
            }
        }
        else if("upload".equals(action)){
            //分片上传过程中出错,有残余时需删除分块后,重新上传
            if (file.exists()) {
                file.delete();
            }

            multipartFile.transferTo(new File(saveDirectory, fileId + "_" + index));

            map = new HashMap<>();
            map.put("flag", "1");
            map.put("fileId", fileId);
            if(!index .equals(total) )
                return map;

        }

        if (path.isDirectory()){
            File[] fileArray = path.listFiles();
            if (fileArray!=null){
                if (fileArray.length == total){
                    //分块全部上传完毕,合并
                    File newFile = new File(saveDirectory, fileId + "." + suffix);
                    FileOutputStream outputStream = new FileOutputStream(newFile, true);//文件追加写入
                    for (int i = 0; i < fileArray.length; i++) {
                        File tmpFile = new File(saveDirectory, fileId + "_" + (i + 1));
                        FileUtils.copyFile(tmpFile,outputStream);
                        //应该放在循环结束删除 可以避免 因为服务器突然中断 导致文件合并失败 下次也无法再次合并
                        tmpFile.delete();
                    }
                    outputStream.close();
//                    //修改FileRes记录为上传成功
//                    UploadFile uploadFile = new UploadFile();
//                    uploadFile.setFileId(fileId);
//                    uploadFile.setFileStatus(2);
//                    uploadFile.setFileName(fileName);
//                    uploadFile.setFileMd5(md5);
//                    uploadFile.setFileSuffix(suffix);
//                    uploadFile.setFilePath(filePath);
//                    uploadFile.setFileSize(size);
//
//                    uploadFileRepository.save(uploadFile);
                    map=new HashMap<>();
                    map.put("fileId", fileId);
                    map.put("flag", "2");
                    return map;

                }else if (index==1){
//                    // 文件第一个分片上传时记录到数据库
//                    UploadFile uploadFile = new UploadFile();
//                    uploadFile.setFileMd5(md5);
//                    String name = NameUtil.getFileNameNoEx(fileName);
//                    if (name.length() > 32) {
//                        name = name.substring(0, 32);
//                    }
//                    uploadFile.setFileName(name);
//                    uploadFile.setFileSuffix(suffix);
//                    uploadFile.setFileId(fileId);
//                    uploadFile.setFilePath(filePath);
//                    uploadFile.setFileSize(size);
//                    uploadFile.setFileStatus(1);
//
//                    uploadFileRepository.save(uploadFile);
                }
            }
        }

        return map;
    }

    public Map<String, Object> findByFileMd5(String md5) {
//        UploadFile uploadFile = uploadFileRepository.findByFileMd5(md5);
        Map<String, Object> map = new HashMap<>();
//        if (uploadFile == null) {
        //没有上传过文件
        map.put("flag", 0);
        map.put("fileId", genUniqueKey());
        map.put("date", simpleDateFormat.format(new Date()));
//        }else{
//            //上传过文件 判断文件现在还是否存在
//            File file = new File(uploadFile.getFilePath());
//            if (!file.exists()){
//                //若不存在
//                map = new HashMap<>();
//                map.put("flag", 0);
//                map.put("fileId", uploadFile.getFileId());
//                map.put("date", simpleDateFormat.format(new Date()));
//            }
//            //若文件存在 判断此时是部分上传了 还是已全部上传
//            else{
//                int fileStatus = uploadFile.getFileStatus().intValue();
//                if (fileStatus==1){
//                    //文件只上传了一部分
//                    map = new HashMap<>();
//                    map.put("flag", 1);
//                    map.put("fileId", uploadFile.getFileId());
//                    map.put("date", simpleDateFormat.format(new Date()));
//                }else if (fileStatus==2){
//                    //文件早已上传完整
//                    map = new HashMap<>();
//                    map.put("flag" , 2);
//                }
//
//            }
//        }
        return map;
    }

    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer num = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(num);
    }
}
