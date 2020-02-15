package com.zhumqs.trust;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mingqizhu
 * @date 20191201
 * 基于朴素贝叶斯的决策理论粗糙集算法进行信任决策
 */
public class TrustDecisionManager {
    /**
     * 信任决策表 key: userId, value: [key: 时间戳end, value: 截止end前的信任决策[key: 三个维度+决策, value: 值]]
     * 三个维度+决策: CooperativeCapacity(C) | PreferenceSimilarity(P) | SocialReciprocity(R) | Decision(S)
     * 决策值: Reliable(1) | Unreliable(0) | Observed(-1)
     */
    private static Map<Integer, Map<Long, Map<String, Integer>>> decision = new LinkedHashMap<>();
    /**
     * 信任矩阵 key: 时间戳end  value: 截止end前的时间戳
     */
    private static Map<Long, List<Integer>> trustMat = new LinkedHashMap<>();

    private Map<String, List<Integer>> makeDecision(int userId, long start, long end) {
        return null;
    }

    private Integer getTrustDecision(int fromUserId, int toUserId) {
        return null;
    }

}
