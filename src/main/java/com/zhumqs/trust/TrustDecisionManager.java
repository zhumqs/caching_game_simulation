package com.zhumqs.trust;

import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.model.*;
import com.zhumqs.util.CsvUtils;
import com.zhumqs.util.DataParseUtils;
import com.zhumqs.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingqizhu
 * @date 20191201
 * 基于朴素贝叶斯的决策理论粗糙集算法进行信任决策
 */
@Slf4j
public class TrustDecisionManager {

    private static List<TrustRecord> existRecords;
    private static CooperativeCapacityCalculator capacityCalculator;
    private static PreferenceSimilarityCalculator similarityCalculator;
    private static SocialReciprocityCalculator reciprocityCalculator;

    public TrustDecisionManager() {
        List<Content> contents = DataParseUtils.getContentsFromCsv();
        List<MobileUser> users = DataParseUtils.getMobileUsersFromCsv();
        List<ContentRequest> requests = DataParseUtils.getRequestFromCsv();
        List<ContentReceive> receives = DataParseUtils.getReceiveFromCsv();
        List<ContentTransmission> transmissions = DataParseUtils.getTransmissionFromCsv();
        existRecords = DataParseUtils.getTrustRecordFromCsv();
        capacityCalculator = new CooperativeCapacityCalculator(requests, receives, users);
        similarityCalculator = new PreferenceSimilarityCalculator(requests, contents);
        reciprocityCalculator = new SocialReciprocityCalculator(requests, transmissions, receives);
    }

    /**
     * 信任决策和信任记录
     * @param fromUserId
     * @param toUserId
     * @param start
     * @param end
     */
    private void makeDecisionAndRecord(int fromUserId, int toUserId, long start, long end) {
        // make decision by prerecords
        double capacity = capacityCalculator.getCooperativeCapacity(fromUserId, toUserId, start, end);
        double similarity = similarityCalculator.getUserPreferenceSimilarity(fromUserId, toUserId, start, end);
        double reciprocity = reciprocityCalculator.getSocialReciprocity(fromUserId, toUserId, start, end);
        double priorProbability = getPriorProbability(fromUserId, toUserId, start, end);
        int decision = getTrustDecision(fromUserId, toUserId, start, end, capacity, similarity, reciprocity, priorProbability);

        // record current decision
        boolean exist = false;
        for (TrustRecord existRecord : existRecords) {
            if (existRecord.getFromUserId() == fromUserId && existRecord.getToUserId() == toUserId) {
                List<TrustRecord.TrustValue> values = existRecord.getValues();
                if (values == null) {
                    log.error("Tht trust relation between {} and {} has not been initialized!", fromUserId, toUserId);
                    values = new ArrayList<>();
                }
                TrustRecord.TrustValue value = new TrustRecord.TrustValue();
                value.setPriorProbability(priorProbability);
                value.setDecision(decision);
                value.setCooperativeCapacity(capacity);
                value.setPreferenceSimilarity(similarity);
                value.setSocialReciprocity(reciprocity);
                value.setTimestamp(end);
                values.add(value);
                existRecord.setValues(values);
                exist = true;
                break;
            }
        }
        if (!exist) {
            TrustRecord newRecord = new TrustRecord();
            newRecord.setFromUserId(fromUserId);
            newRecord.setToUserId(toUserId);
            List<TrustRecord.TrustValue> values = new ArrayList<>();
            TrustRecord.TrustValue value = new TrustRecord.TrustValue();
            value.setPriorProbability(priorProbability);
            value.setCooperativeCapacity(capacity);
            value.setDecision(decision);
            value.setPreferenceSimilarity(similarity);
            value.setSocialReciprocity(reciprocity);
            value.setTimestamp(end);
            values.add(value);
            newRecord.setValues(values);
            existRecords.add(newRecord);
        }
        String csvFileName = "trust_record.csv";
        String csvPath = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName;
        CsvUtils.writeCsv(existRecords, csvPath);
    }

    /**
     * 根据多维信任值和先验概率进行信任决策
     * @param fromUserId
     * @param toUserId
     * @param start
     * @param end
     * @param capacity
     * @param similarity
     * @param reciprocity
     * @param priorProbability
     * @return
     */
    private int getTrustDecision(int fromUserId, int toUserId, long start, long end,
                                 double capacity, double similarity, double reciprocity, double priorProbability) {
        double posteriorProbability = getPosteriorProbability(fromUserId, toUserId, start, end,
                capacity, similarity, reciprocity, priorProbability);
        double positiveBound = getPositiveBound(capacity, similarity, reciprocity);
        double negativeBound = getNegativeBound(capacity, similarity, reciprocity);
        if (positiveBound < negativeBound) {
            return -1;
        }

        int decision = 2;
        if (posteriorProbability >= positiveBound) {
            decision = 1;
        }
        if (posteriorProbability < negativeBound) {
            decision = 0;
        }
        return decision;
    }

    /**
     * alpha(α') = log(p(s'))/log(p(s)) + log(α/(1-α)) = log(p([a,b,c]|s)/p([a,b,c]|s')) 正向边界
     * @param capacity
     * @param similarity
     * @param reciprocity
     * @return
     */
    private double getPositiveBound(double capacity, double similarity, double reciprocity) {
        return getLikelihoodRatio(capacity * similarity * reciprocity);
    }

    /**
     * bate(β') 负向边界
     * @param capacity
     * @param similarity
     * @param reciprocity
     * @return
     */
    private double getNegativeBound(double capacity, double similarity, double reciprocity) {
        return getLikelihoodRatio(capacity * similarity * reciprocity);
    }

    /**
     * p(s|[a,b,c]) = p([a,b,c]|s) * p(s) / p(a,b,c) 后验概率
     * p([a,b,c]|s) = p(a|s) * p(b|s) * p(c|s) 条件概率
     * @param fromUserId
     * @param toUserId
     * @param start
     * @param end
     * @param capacity
     * @param similarity
     * @param reciprocity
     * @return
     */
    private double getPosteriorProbability(int fromUserId, int toUserId, long start, long end,
                                           double capacity, double similarity, double reciprocity, double priorProbability) {
        long current = System.currentTimeMillis();
        double unconditionedProbability = capacityCalculator.getCooperativeCapacity(fromUserId, toUserId, 0, current)
                * similarityCalculator.getUserPreferenceSimilarity(fromUserId, toUserId, 0, current)
                * reciprocityCalculator.getSocialReciprocity(fromUserId, toUserId, 0, current);
        if (unconditionedProbability == 0) {
            return 0;
        }
        return (capacity * similarity * reciprocity * priorProbability) / unconditionedProbability;
    }

    /**
     * p(s) 先验概率
     * @param fromUserId
     * @param toUserId
     * @param start
     * @param end
     * @return
     */
    private double getPriorProbability(int fromUserId, int toUserId, long start, long end) {
        // 默认值为0.5
        double priorProbability = 0.5;
        for (TrustRecord existRecord : existRecords) {
            if (existRecord.getFromUserId() == fromUserId && existRecord.getToUserId() == toUserId) {
                List<TrustRecord.TrustValue> values = existRecord.getValues();
                priorProbability = values.get(values.size() - 1).getPriorProbability();
                break;
            }
        }
        return priorProbability;
    }

    /**
     * log(p/(1-p)) 似然比计算
     * @param probability
     * @return
     */
    private double getLikelihoodRatio(double probability) {
        return Math.log(probability / (1 - probability));
    }

    public static void main(String[] args) {
        TrustDecisionManager manager = new TrustDecisionManager();
        int fromUserId = RandomUtils.getRandomInterval(1, 75);
        int toUserId = RandomUtils.getRandomInterval(1, 75);
        while (fromUserId == toUserId) {
            fromUserId = RandomUtils.getRandomInterval(1, 75);
        }
        manager.makeDecisionAndRecord(fromUserId, toUserId, System.currentTimeMillis() - 5 * 60 * 60 * 1000, System.currentTimeMillis());
        log.info("trust decision is done!");
    }

}
