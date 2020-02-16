package com.zhumqs.trust;

import com.zhumqs.model.Content;
import com.zhumqs.model.ContentRequest;
import com.zhumqs.util.DataParseUtils;
import com.zhumqs.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingqi zhu
 * @date 20191201
 * @description 根据用户历史请求记录计算偏好相似度
 */
@Slf4j
public class PreferenceSimilarityCalculator {

    private static List<ContentRequest> requestList = new ArrayList<ContentRequest>();
    private static List<Content> defaultContents = new ArrayList<Content>();

    public PreferenceSimilarityCalculator() {
        requestList = DataParseUtils.getRequestFromCsv();
        defaultContents = DataParseUtils.getContentsFromCsv();
    }

    public double getUserPreferenceSimilarity(int userId1, int userId2, long start, long end) {
        if (defaultContents == null || defaultContents.size() == 0) {
            log.error("content.csv is empty!");
            return 0.0;
        }
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum3 = 0.0;
        for (Content content : defaultContents) {
            double d1 = getUserPreferenceToContent(userId1, start, end, content);
            double d2 = getUserPreferenceToContent(userId2, start, end, content);
            sum1 += d1 * d2;
            sum2 += Math.pow(d1, 2);
            sum3 += Math.pow(d2, 2);
        }
        return sum1 / (sum2 * sum3);
    }

    public double getUserPreferenceToContent(int userId, long start, long end, Content content) {
        double preferenceSum = 0.0;
        List<Integer> themeList = content.getThemeList();
        for (Integer theme : themeList) {
            preferenceSum += getUserPreferenceToTheme(userId, start, end, theme);
        }
        if (preferenceSum <= 0) {
            return 0.0;
        }
        return preferenceSum / (Math.sqrt(preferenceSum) * Math.sqrt(themeList.size()));
    }

    private double getUserPreferenceToTheme(int userId, long start, long end, int theme) {
        List<Content> historyRequestContents = getHistoryRequestContentsInTime(userId, start, end);
        double conditionalProbability = getThemeProbability(historyRequestContents, theme);
        double unconditionalProbability = getThemeProbability(defaultContents, theme);
        if (unconditionalProbability == 0) {
            return 0.0;
        }
        return Math.log(conditionalProbability / unconditionalProbability);
    }

    private double getThemeProbability(List<Content> contents, int theme) {
        if (contents == null || contents.size() ==0) {
            return 0.0;
        }
        int count = 0;
        for (Content content : contents) {
            for (Integer key : content.getThemeList()) {
                if (key == theme) {
                    count++;
                    break;
                }
            }
        }
        return count * 1.0 / contents.size();
    }

    private List<Content> getHistoryRequestContentsInTime(int userId, long start, long end) {
        List<Content> requestContents = new ArrayList<Content>();
        for (ContentRequest request : requestList) {
            if (request.getRequestUserId() == userId
                    && request.getCreated() >= start
                    && request.getCreated() <= end) {
                for (Content c : defaultContents) {
                    if (c.getContentId().equals(request.getContentId())) {
                        requestContents.add(c);
                        break;
                    }
                }
            }
        }
        return requestContents;
    }

    public static void main(String[] args) {
        PreferenceSimilarityCalculator calculator = new PreferenceSimilarityCalculator();
        int fromUserId = RandomUtils.getRandomInterval(1, 75);
        int toUserId = RandomUtils.getRandomInterval(1, 75);
        while (fromUserId == toUserId) {
            fromUserId = RandomUtils.getRandomInterval(1, 75);
        }
        double similarity = calculator.getUserPreferenceSimilarity(fromUserId, toUserId, RandomUtils.getRandom(1000), System.currentTimeMillis());
        log.info("Preference similarity between {} and {} is {}.", fromUserId, toUserId, similarity);
    }
}
