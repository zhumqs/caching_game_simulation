package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Data
public class Message {
    private Integer msgId;
    private Integer srcUserId;
    private Integer created;
    private String type;
    private Integer dst;
}
