package com.zjz.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ZjzUploadRequest {
    private MultipartFile data;
}
