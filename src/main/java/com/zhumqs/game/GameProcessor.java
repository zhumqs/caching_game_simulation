package com.zhumqs.game;

import com.zhumqs.model.Content;
import com.zhumqs.model.MobileUser;
import com.zhumqs.trust.PreferenceSimilarityCalculator;
import com.zhumqs.util.DataParseUtils;

import java.util.*;

/**
 * @author mingqi zhu
 * @date 20191201
 */
public class GameProcessor {
    private static List<MobileUser> mobileUsers;
    private static List<Content> contents;
    private static Map<Content, Map<MobileUser, List<MobileUser>>> cacheMap;
    private static Map<Content, Map<MobileUser, MobileUser>> nocacheMap;
    private static Map<Content, Map<MobileUser, Double>> costMap;
    private static PreferenceSimilarityCalculator preferenceCalculator;
    private static GameManager gameManager;

    public GameProcessor() {
        mobileUsers = DataParseUtils.getMobileUsersFromCsv();
        contents = DataParseUtils.getContentsFromCsv();
        cacheMap = new HashMap<>();
        nocacheMap = new HashMap<>();
        costMap = new HashMap<>();
        preferenceCalculator = new PreferenceSimilarityCalculator();
        gameManager = new GameManager();
    }

    public void process(long start, long end) {
        for (Content content : contents) {
            contentPlacement(content, start, end);
        }
    }

    private void contentPlacement(Content content, long start, long end) {
        Map<MobileUser, Double> preferenceMap = new HashMap<>();
        for (MobileUser user : mobileUsers) {
            double preference = preferenceCalculator.getUserPreferenceToContent(user.getUserId(), start, end, content);
            preferenceMap.put(user, preference);
        }

        List<MobileUser> userList = new ArrayList<>(mobileUsers);
        Map<MobileUser, List<MobileUser>> externalCacheMap = new HashMap<>();
        Map<MobileUser, MobileUser> externalNocacheMap = new HashMap<>();
        Map<MobileUser, Double> externalCostMap = new HashMap<>();
        while (userList.size() > 0) {
            // 获取最感兴趣的用户作为缓存用户
            MobileUser cacheUser = new MobileUser();
            double maxPreference = Double.MIN_VALUE;
            int cacheIndex = 0;
            for (int i = 0; i < userList.size(); i++) {
                MobileUser user = userList.get(i);
                if (preferenceMap.containsKey(user)) {
                    double preference = preferenceMap.get(user);
                    if (preference > maxPreference) {
                        cacheUser = user;
                        maxPreference = preference;
                        cacheIndex = i;
                    }
                }
            }
            userList.remove(cacheIndex);
            double cacheCost = gameManager.getCacheCost(content.getContentId());
            externalCostMap.put(cacheUser,cacheCost);

            List<MobileUser> nocacheUserList = new ArrayList<>();
            Iterator<MobileUser> iterator = userList.iterator();
            while (iterator.hasNext()) {
                MobileUser user = iterator.next();
                double nocacheCost = gameManager.getNocacheCost(cacheUser.getUserId(), user.getUserId(), start, end, content);
                if (nocacheCost < cacheCost) {
                    nocacheUserList.add(user);
                    externalCostMap.put(user, nocacheCost);
                    iterator.remove();
                }
            }
            // update cacheMap
            externalCacheMap.put(cacheUser,nocacheUserList);
            cacheMap.put(content, externalCacheMap);
            // update nocacheMap
            for (MobileUser user : nocacheUserList) {
                externalNocacheMap.put(user, cacheUser);
            }
            nocacheMap.put(content, externalNocacheMap);
            // update costMap
            costMap.put(content, externalCostMap);
        }
    }

    private MobileUser getMaxPreferenceUser(Map<MobileUser, Double> preferenceMap, List<MobileUser> userList) {
        MobileUser maxUser = null;
        double maxPreference = Double.MIN_VALUE;
        for (MobileUser user : userList) {
            if (preferenceMap.containsKey(user)) {
                double preference = preferenceMap.get(user);
                if (preference > maxPreference) {
                    maxUser = user;
                }
            }
        }
        return maxUser;
    }
}
