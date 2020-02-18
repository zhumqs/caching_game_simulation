package com.zhumqs.game;

import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.model.Content;
import com.zhumqs.model.ContentRequest;
import com.zhumqs.model.MobileUser;
import com.zhumqs.model.TrustRecord;
import com.zhumqs.trust.PreferenceSimilarityCalculator;
import com.zhumqs.util.DataParseUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Slf4j
public class GameProcessor {
    private static List<MobileUser> mobileUsers;
    private static List<Content> contents;
    private static List<TrustRecord> trustRecords;
    private static List<ContentRequest> requests;
    private static Map<Integer, Map<Integer, List<Integer>>> cacheMap;
    private static Map<Integer, Map<Integer, Integer>> nocacheMap;
    private static Map<Integer, Map<Integer, Double>> costMap;
    private static Map<Integer, Map<Integer, Double>> preferenceMap;
    private static PreferenceSimilarityCalculator preferenceCalculator;
    private static GameManager gameManager;

    public GameProcessor() {
        mobileUsers = DataParseUtils.getMobileUsersFromCsv();
        contents = DataParseUtils.getContentsFromCsv();
        trustRecords = DataParseUtils.getTrustRecordFromCsv();
        requests = DataParseUtils.getRequestFromCsv();
        cacheMap = new HashMap<>();
        nocacheMap = new HashMap<>();
        costMap = new HashMap<>();
        preferenceMap = new HashMap<>();
        preferenceCalculator = new PreferenceSimilarityCalculator(requests, contents);
        gameManager = new GameManager();
    }

    public void process(long start, long end) {
        for (Content content : contents) {
            contentPlacement(content, start, end);
        }
    }

    private void contentPlacement(Content content, long start, long end) {
        Map<Integer, Double> internalPreferenceMap = new HashMap<>();
        for (MobileUser user : mobileUsers) {
            double preference = preferenceCalculator.getUserPreferenceToContent(user.getUserId(), start, end, content);
            internalPreferenceMap.put(user.getUserId(), preference);
        }

        List<MobileUser> userList = new ArrayList<>(mobileUsers);
        Map<Integer, List<Integer>> internalCacheMap = new HashMap<>();
        Map<Integer, Integer> internalNocacheMap = new HashMap<>();
        Map<Integer, Double> internalCostMap = new HashMap<>();
        while (userList.size() > 0) {
            // 1. 获取最感兴趣的用户作为缓存用户
            int cacheUser = 0;
            double maxPreference = Double.MIN_VALUE;
            int cacheIndex = 0;
            for (int i = 0; i < userList.size(); i++) {
                int userId = userList.get(i).getUserId();
                if (internalPreferenceMap.containsKey(userId)) {
                    double preference = internalPreferenceMap.get(userId);
                    if (preference > maxPreference) {
                        cacheUser = userId;
                        maxPreference = preference;
                        cacheIndex = i;
                    }
                }
            }
            userList.remove(cacheIndex);
            double cacheCost = gameManager.getCacheCost(content.getContentId());
            internalCostMap.put(cacheUser,cacheCost);

            // 2. 根据信任关系和成本确定非缓存用户
            List<Integer> nocacheUserList = new ArrayList<>();
            Iterator<MobileUser> iterator = userList.iterator();
            while (iterator.hasNext()) {
                MobileUser user = iterator.next();
                boolean trustFlag = false;
                for (TrustRecord record : trustRecords) {
                    if (record.getFromUserId() == cacheUser && record.getToUserId() == user.getUserId()) {
                        List<TrustRecord.TrustValue> values = record.getValues();
                        TrustRecord.TrustValue value = values.get(values.size() - 1);
                        if (value.getDecision() == 1){
                            trustFlag = true;
                        }
                        break;
                    }
                }
                double nocacheCost = gameManager.getNocacheCost(cacheUser, user.getUserId(), start, end, content);
                if (trustFlag && nocacheCost < cacheCost) {
                    nocacheUserList.add(user.getUserId());
                    internalCostMap.put(user.getUserId(), nocacheCost);
                    iterator.remove();
                }
            }
            // update cacheMap
            internalCacheMap.put(cacheUser,nocacheUserList);
            cacheMap.put(content.getContentId(), internalCacheMap);
            // update nocacheMap
            for (Integer userId : nocacheUserList) {
                internalNocacheMap.put(userId, cacheUser);
            }
            nocacheMap.put(content.getContentId(), internalNocacheMap);
            // update costMap
            costMap.put(content.getContentId(), internalCostMap);
            // update preferenceMap
            preferenceMap.put(content.getContentId(), internalPreferenceMap);
        }
    }

    public double getTotalCost() {
        double totalCost = 0.0;
        for (Map.Entry<Integer, Map<Integer, Double>> entry : costMap.entrySet()) {
            Map<Integer, Double> value = entry.getValue();
            for (Map.Entry<Integer, Double> entry1 : value.entrySet()) {
                totalCost += entry1.getValue();
            }
        }
        return totalCost;
    }

    public double getCacheHitRatio() {
        double numerator = 0.0, denominator = 0.0;
        for (Map.Entry<Integer, Map<Integer, Double>> entry : preferenceMap.entrySet()) {
            Integer contentId = entry.getKey();
            Map<Integer, Double> value = entry.getValue();
            for (Map.Entry<Integer, Double> entry1 : value.entrySet()) {
                int userId = entry1.getKey();
                double preference = entry1.getValue();
                if (cacheMap.get(contentId).containsKey(userId)) {
                    numerator += (1 - Math.pow(Math.E, -1 * Math.pow(ExperimentConstants.D2D_RANGE, 2))) * preference;
                }
                denominator += preference;
            }
        }
        return numerator / denominator;
    }

    private double getAverageAccessDelay() {
        double total = 0.0;
        for (Map.Entry<Integer, Map<Integer, Double>> entry : preferenceMap.entrySet()) {
            Integer contentId = entry.getKey();
            for (Map.Entry<Integer, Double> entry1 : entry.getValue().entrySet()) {
                int userId = entry1.getKey();
                double preference = entry1.getValue();
                double dataRate;
                if (cacheMap.get(contentId).containsKey(userId)) {
                    dataRate = getDataRate(ExperimentConstants.D2D_TRANSMIT_POWER,
                            ExperimentConstants.D2D_BANDWIDTH);
                } else {
                    dataRate = getDataRate(ExperimentConstants.BS_TRANSMIT_POWER,
                            ExperimentConstants.MACRO_BANDWIDTH);
                }
                total += (preference * ExperimentConstants.CONTENT_DEFAULT_SIZE / dataRate);
            }
        }
        return total / (mobileUsers.size() * contents.size());
    }

    private double getDataRate(double transmitPower, double bandwidth) {
        return bandwidth * Math.log(1 + (transmitPower / ExperimentConstants.NOISE_POWER));
    }

    public static void main(String[] args) {
        GameProcessor processor = new GameProcessor();
        log.info("Total cost is {},\n Cache hit ratio is {},\n Average access delay is {}.",
                processor.getTotalCost(), processor.getCacheHitRatio(), processor.getAverageAccessDelay());
    }
}
