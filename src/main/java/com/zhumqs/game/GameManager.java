package com.zhumqs.game;

import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.model.Content;
import com.zhumqs.model.MobileUser;
import com.zhumqs.model.TrustRecord;
import com.zhumqs.trust.PreferenceSimilarityCalculator;
import com.zhumqs.util.DataParseUtils;
import com.zhumqs.util.LocationUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mingqi zhu
 * @date 20191202
 */
@Slf4j
public class GameManager {

    private static List<TrustRecord> trustRecords = new ArrayList<>();
    private static List<MobileUser> mobileUsers = new ArrayList<>();
    private static PreferenceSimilarityCalculator similarityCalculator;

    public GameManager() {
        trustRecords = DataParseUtils.getTrustRecordFromCsv();
        mobileUsers = DataParseUtils.getMobileUsersFromCsv();
        similarityCalculator = new PreferenceSimilarityCalculator();
    }

    public double getNocacheCost(int fromUserId, int toUserId, long start, long end, Content content) {
        double preference = similarityCalculator.getUserPreferenceToContent(fromUserId, start, end, content);
        double totalDistance = getTotalDistance(fromUserId, toUserId, start, end);
        return preference * totalDistance;
    }

    public double getCacheCost(int contentId) {
        return ExperimentConstants.PLACEMENT_COST + contentId >= 100 ? 2.0 : 1.0;
    }

    public double getTotalDistance(int fromUserId, int toUserId, long start, long end) {
        double physicalDistance = getPhysicalDistance(fromUserId, toUserId);
        double socialDistance = getSocialDistance(fromUserId, toUserId, start, end);
        if (socialDistance == 0) {
            return Double.MAX_VALUE;
        }
        return physicalDistance / socialDistance;
    }

    private double getPhysicalDistance(int fromUserId, int toUserId) {
        double d1 = 0.0, d2 = 0.0, d3 = 0.0, d4 = 0.0;
        for (MobileUser user : mobileUsers) {
            int userId = user.getUserId();
            if (userId == fromUserId) {
                d1 = user.getLatitude();
                d2 = user.getLongitude();
            }
            if (userId == toUserId) {
                d3 = user.getLatitude();
                d4 = user.getLongitude();
            }
        }
        return LocationUtils.getDistance(d1, d2, d3, d4);
    }

    private double getSocialDistance(int fromUserId, int toUserId, long start, long end) {
        Map<String, Double> valueMap= getStandardValue(start, end);
        double d1 = 0.0, d2 = 0.0, d3 = 0.0;
        for (TrustRecord record : trustRecords) {
            if (record.getFromUserId() == fromUserId && record.getToUserId() == toUserId) {
                List<TrustRecord.TrustValue> values = record.getValues();
                TrustRecord.TrustValue value = values.get(values.size() - 1);
                d1 += value.getCooperativeCapacity() / valueMap.get("cooperativeCapacity");
                d2 += value.getPreferenceSimilarity() / valueMap.get("preferenceSimilarity");
                d3 += value.getPriorProbability() / valueMap.get("socialReciprocity");
                break;
            }
        }
        return d1 * 1/3 + d2 * 1/3 + d3 * 1/3;
    }

    private Map<String, Double> getStandardValue(long start, long end) {
        double totalCapacity = 0.0, totalSimilarity = 0.0, totalReciprocity = 0.0;
        for (TrustRecord record : trustRecords) {
            List<TrustRecord.TrustValue> values = record.getValues();
            TrustRecord.TrustValue value = values.get(values.size() - 1);
            totalCapacity += value.getCooperativeCapacity();
            totalSimilarity += value.getPreferenceSimilarity();
            totalReciprocity += value.getSocialReciprocity();
        }
        Map<String, Double> valueMap = new HashMap<>();
        int count = trustRecords.size();
        valueMap.put("cooperativeCapacity", totalCapacity / count);
        valueMap.put("preferenceSimilarity", totalSimilarity / count);
        valueMap.put("socialReciprocity", totalReciprocity / count);
        return valueMap;
    }
}

