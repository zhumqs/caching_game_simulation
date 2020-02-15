package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Data
public class ContentReceive {
    private Integer contentId;
    private Integer dstUserId;// 接受者
    private Integer srcUserId;// 发送者
    private Integer type; //0代表发送者是D2D用户则srcUserId不为空 1代表发送者为BS则srcUserId为0
    private long timestamp;
}
