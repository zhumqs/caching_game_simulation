package com.zhumqs.trust;

import com.zhumqs.model.ContentReceive;
import com.zhumqs.model.ContentRequest;
import com.zhumqs.model.MobileUser;
import com.zhumqs.util.DataParseUtils;
import com.zhumqs.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author mingqi zhu
 * @date 20191201
 * @description 根据用户的历史交互记录计算合作能力
 */
@Slf4j
public class CooperativeCapacityCalculator {

    private List<ContentRequest> requestList;
    private List<ContentReceive> receiveList;
    private List<MobileUser> mobileUsers;

    public CooperativeCapacityCalculator(List<ContentRequest> requestList,
                                         List<ContentReceive> receiveList,
                                         List<MobileUser> mobileUsers) {
        this.receiveList = receiveList;
        this.requestList = requestList;
        this.mobileUsers = mobileUsers;
    }

    public double getCooperativeCapacity(int fromUserId, int toUserId, long start, long end) {
        int interactiveNumber = getInteractiveNumber(fromUserId, toUserId, start, end);
        double w1 = 1 - Math.pow(Math.E, -interactiveNumber);
        double w2 = 1 - w1;
        return w1 * getDirectCapacity(fromUserId, toUserId, start, end)
                + w2 * getInDirectCapacity(fromUserId, toUserId, start, end);
    }

    private double getDirectCapacity(int fromUserId, int toUserId, long start, long end) {
        int receiveNumber = getReceiveNumberInTime(fromUserId, toUserId, start, end);
        int requestNumber = getRequestNumberInTime(fromUserId, toUserId, start, end);
        if (requestNumber == 0) {
            return 0;
        }
        return (receiveNumber*1.0) / requestNumber;
    }

    private double getInDirectCapacity(int fromUserId, int toUserId, long start, long end) {
        Map<Integer, Double> auxiliaryUserMap = getAuxiliaryUserMap(fromUserId, toUserId, start, end);
        if (auxiliaryUserMap == null || auxiliaryUserMap.size() == 0) {
            return 0;
        }
        double totalAuxiliaryCapacity = 0.0;
        int auxiliaryCount = 0;
        for (Map.Entry<Integer, Double> entry : auxiliaryUserMap.entrySet()) {
            totalAuxiliaryCapacity += entry.getValue();
            auxiliaryCount++;
        }
        return totalAuxiliaryCapacity / auxiliaryCount;
    }

    private Map<Integer, Double> getAuxiliaryUserMap(int fromUserId, int toUserId, long start, long end) {
        Map<Integer, Double> auxiliaryMap = new HashMap<Integer, Double>();
        Map<Integer, Double> capacityMap = new HashMap<Integer, Double>();
        double totalCapacity = 0.0;
        int userCount = 0;

        for (MobileUser mobileUser : mobileUsers) {
            int userId = mobileUser.getUserId();
            if (userId == fromUserId || userId == toUserId) {
                continue;
            }
            double capacity = getDirectCapacity(toUserId, userId, start, end);
            capacityMap.put(userId, capacity);
            totalCapacity += capacity;
            userCount++;
        }
        double averageCapacity = totalCapacity / userCount;

        for (Map.Entry<Integer, Double> entry : capacityMap.entrySet()) {
            double capacity = entry.getValue();
            if (capacity >= averageCapacity) {
                auxiliaryMap.put(entry.getKey(), entry.getValue());
            }
        }
        return auxiliaryMap;
    }

    private int getReceiveNumberInTime(int fromUserId, int toUserId, long start, long end) {
        int count = 0;
        for (ContentReceive receive : receiveList) {
            if (receive.getSrcUserId() == toUserId
                    && receive.getDstUserId() == fromUserId
                    && receive.getTimestamp() >= start
                    && receive.getTimestamp() <= end) {
                count++;
            }
        }
        return count;
    }

    private int getRequestNumberInTime(int fromUserId, int toUserId, long start, long end) {
        int count = 0;
        for (ContentRequest request : requestList) {
            if (request.getRequestUserId() == fromUserId
                    && request.getDstUserId() == toUserId
                    && request.getCreated() >= start
                    && request.getCreated() <= end) {
                count++;
            }
        }
        return count;
    }

    private int getInteractiveNumber(int fromUserId, int toUserId, long start, long end) {
        return getRequestNumberInTime(fromUserId, toUserId, start, end)
                + getReceiveNumberInTime(fromUserId, toUserId, start, end);
    }

    public static void main(String[] args) {
        CooperativeCapacityCalculator calculator = new CooperativeCapacityCalculator(DataParseUtils.getRequestFromCsv(),
                DataParseUtils.getReceiveFromCsv(), DataParseUtils.getMobileUsersFromCsv());
        int fromUserId = RandomUtils.getRandomInterval(1, 75);
        int toUserId = RandomUtils.getRandomInterval(1, 75);
        while (fromUserId == toUserId) {
            fromUserId = RandomUtils.getRandomInterval(1, 75);
        }
        double capacity = calculator.getCooperativeCapacity(fromUserId, toUserId, RandomUtils.getRandom(1000), System.currentTimeMillis());
        log.info("cooperative capacity between {} and {} is {}.", fromUserId, toUserId, capacity);
    }
}
