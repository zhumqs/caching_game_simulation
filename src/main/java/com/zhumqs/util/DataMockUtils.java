package com.zhumqs.util;

import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.model.Content;
import com.zhumqs.model.MobileUser;
import com.zhumqs.model.TrustRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mingqizhu
 * @date 20191204
 */
@Data
public class DataMockUtils {

    public static List<Content> mockContent() {
        List<Content> contents = new ArrayList<Content>();
        for (int i = 1; i <= 200; i++) {
            Content content = new Content();
            content.setContentId(i);
            int size = (int)(1+Math.random()*(5-1+1));
            List<Integer> themeList = new ArrayList<Integer>();
            for (int j = 0; j < size; j++) {
                themeList.add((int)(1+Math.random()*(30-1+1)));
            }
            content.setThemeList(themeList);
            content.setSize(ExperimentConstants.CONTENT_DEFAULT_SIZE);
            contents.add(content);
        }
        return contents;
    }

    public static List<TrustRecord> mockTrustRecord() {
        List<TrustRecord> mockRecords = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            for (int j = 1; j <= 75; j++) {
                TrustRecord record = new TrustRecord();
                record.setFromUserId(i);
                record.setToUserId(j);
                List<TrustRecord.TrustValue> values = new ArrayList<>();
                TrustRecord.TrustValue v1 = new TrustRecord.TrustValue();
                // 0: unreliable 1: reliable 2: observed
                v1.setDecision(RandomUtils.getRandom(3));
                v1.setPriorProbability(0.5);
                v1.setTimestamp(System.currentTimeMillis() - 5 * 60 * 60 * 1000);
                v1.setSocialReciprocity(0);
                v1.setCooperativeCapacity(0);
                v1.setPreferenceSimilarity(0);
                values.add(v1);

                TrustRecord.TrustValue v2 = new TrustRecord.TrustValue();
                // 0: unreliable 1: reliable 2: observed
                v2.setDecision(RandomUtils.getRandom(3));
                v2.setPriorProbability(0.5);
                v2.setTimestamp(System.currentTimeMillis() - 5 * 60 * 60 * 1000);
                v2.setSocialReciprocity(0);
                v2.setPreferenceSimilarity(0);
                v2.setCooperativeCapacity(0);
                values.add(v2);

                record.setValues(values);
                mockRecords.add(record);
            }
        }
        return mockRecords;
    }

    // 32.114967(经度:Longitude),118.928547(纬度:Latitude)
    public static List<MobileUser> mockMobileUsers() {
        List<MobileUser> users = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            MobileUser user = new MobileUser();
            user.setUserId(i);
            user.setCity(RandomUtils.getRandomInterval(1, 20));
            user.setInstitute(RandomUtils.getRandomInterval(1, 30));
            user.setCountry(RandomUtils.getRandomInterval(1, 20));
            user.setLongitude(ExperimentConstants.BASE_STATION_LOCATION_LONGITUDE
                    + RandomUtils.getRandomInterval(10, 800) * 0.000001 * RandomUtils.getPlusOrMinus());
            user.setLatitude(ExperimentConstants.BASE_STATION_LOCATION_LATITUDE
                    + RandomUtils.getRandomInterval(10, 800) * 0.000001 * RandomUtils.getPlusOrMinus());
            users.add(user);
        }
        return users;
    }
}
