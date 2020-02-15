package com.zhumqs.model;

import lombok.Data;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Data
public class Proximity {
    private Integer timestamp;
    private Integer userId;
    private Integer seenUserId;
    private Integer seenDeviceMajorCod;
    private Integer seenDeviceMinorCod;
}
