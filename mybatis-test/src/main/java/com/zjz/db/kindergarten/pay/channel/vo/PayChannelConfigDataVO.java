package com.zjz.db.kindergarten.pay.channel.vo;

import lombok.Data;

/**
 * @author zjz
 * @date 2021/11/8 19:16
 */
@Data
public class PayChannelConfigDataVO {
    private Long kindergartenId;
    private String channelCode;
    private String channelName;
    private Integer status;
}
