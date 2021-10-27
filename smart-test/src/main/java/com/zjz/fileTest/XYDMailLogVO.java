package com.zjz.fileTest;

import lombok.Data;

/**
 * @author com.zjz
 * @date 2021/10/11 17:24
 */
@Data
public class XYDMailLogVO {
    private String receiveMail;

    private String amount;

    private String sendTime;

    private Integer status;

    private Integer anchor;
}
