package com.zhumqs.model;

import lombok.Data;

import java.util.List;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Data
public class TrustRecord {
    private int fromUserId;
    private int toUserId;
    private List<TrustValue> values;

    @Data
    public static class TrustValue {
        private double preferenceSimilarity;
        private double cooperativeCapacity;
        private double socialReciprocity;
        private int decision;
        private double priorProbability;//初始默认我0.5
        private long timestamp;
    }
}
