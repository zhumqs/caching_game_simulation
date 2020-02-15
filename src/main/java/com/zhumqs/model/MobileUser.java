package com.zhumqs.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Data
public class MobileUser {
    private Integer userId;
    private Integer institute;
    private Integer city;
    private Integer country;
    private double longitude; // 经度
    private double latitude; // 纬度
}
