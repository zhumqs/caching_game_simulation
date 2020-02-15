package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Data
public class ContentTransmission {
    private Integer contentId;
    private Integer bytes;
    private Integer srcUserId;
    private Integer dstUserId;
    private Integer type; //0发送者为D2D用户则srcUserId不为空 1发送者为BS则srcUserId为0
    private long timestamp;
}
