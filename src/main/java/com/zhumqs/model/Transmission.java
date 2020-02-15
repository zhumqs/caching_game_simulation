package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Data
public class Transmission {
    private Integer type;
    private Integer msgId;
    private Integer bytes;
    private Integer hopSrcUserId;
    private Integer hopDstUserId;
    private Integer srcTimestamp;
    private Integer status;
}
