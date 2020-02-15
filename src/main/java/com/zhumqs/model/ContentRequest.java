package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Data
public class ContentRequest {
    private Integer requestUserId; // 请求者
    private Integer type; //0代表向用户请求则dstUserId不为空 1代表向BS请求则dstUserId为0
    private Integer contentId;
    private Integer dstUserId; // 被请求者
    private long created;
}
