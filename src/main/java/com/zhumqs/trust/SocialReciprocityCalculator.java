package com.zhumqs.trust;

import com.zhumqs.model.ContentReceive;
import com.zhumqs.model.ContentRequest;
import com.zhumqs.model.ContentTransmission;
import com.zhumqs.util.DataParseUtils;
import com.zhumqs.util.MathUtils;
import com.zhumqs.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author mingqi zhu
 * @date 20191201
 * @description 根据用户历史互助行为计算社会互惠性
 */
@Slf4j
public class SocialReciprocityCalculator {

    private List<ContentRequest> requestList;
    private List<ContentTransmission> transmissionList;
    private List<ContentReceive> receiveList;

    public SocialReciprocityCalculator(List<ContentRequest> requestList,
                                       List<ContentTransmission> transmissionLis,
                                       List<ContentReceive> receiveList) {
        this.requestList = requestList;
        this.receiveList = receiveList;
        this.transmissionList = transmissionLis;
    }

    public double getSocialReciprocity(int fromUserId, int toUserId, long start, long end) {
        double mutualAidInterval = getAverageMutualAidInterval(fromUserId, toUserId, start, end);
        if (mutualAidInterval == 0) {
            return 0;
        }
        return (2/Math.PI) * MathUtils.arccot(mutualAidInterval);
    }

    private double getAverageMutualAidInterval(int fromUserId, int toUserId, long start, long end) {
        Set<Long> timestampSet = new TreeSet<Long>();
        timestampSet.addAll(getRequestTimestampBetweenUsers(fromUserId, toUserId, start, end));
        timestampSet.addAll(getTransmissionTimestampBetweenUsers(fromUserId, toUserId, start, end));
        timestampSet.addAll(getReceiveTimeStampBetweenUsers(fromUserId, toUserId, start, end));

        int size = timestampSet.size();
        if (size == 0 || size == 1) {
            return 0;
        }
        List<Long> timestampList = new ArrayList<Long>(timestampSet);
        return (timestampList.get(size - 1) - timestampList.get(0)) * 1.0 / (size - 1);
    }

    private List<Long> getRequestTimestampBetweenUsers(int userId1, int userId2, long start, long end) {
        List<Long> requestTimestampList = new ArrayList<Long>();
        for (ContentRequest request : requestList) {
            int dstUserId = request.getDstUserId();
            int requestId = request.getRequestUserId();
            long timestamp = request.getCreated();
            boolean b1 = (userId1 == dstUserId && userId2 == requestId) || (userId1 == requestId && userId2 == dstUserId);
            boolean b2 = timestamp >= start && timestamp <= end;
            if (b1 && b2) {
                requestTimestampList.add(timestamp);
            }
        }
        return requestTimestampList;
    }

    private List<Long> getTransmissionTimestampBetweenUsers(int userId1, int userId2, long start, long end) {
        List<Long> transmissionTimestampList = new ArrayList<Long>();
        for (ContentTransmission transmission : transmissionList) {
            int dstUserId = transmission.getDstUserId();
            int srcUserId = transmission.getSrcUserId();
            long timestamp = transmission.getTimestamp();
            boolean b1 = timestamp >= start && timestamp <= end;
            boolean b2 = (userId1 == dstUserId && userId2 == srcUserId) || (userId1 == srcUserId && userId2 == dstUserId);
            if (b1 && b2) {
                transmissionTimestampList.add(timestamp);
            }
        }
        return transmissionTimestampList;
    }

    private List<Long> getReceiveTimeStampBetweenUsers(int userId1, int userId2, long start, long end) {
        List<Long> receiveTimeStampList = new ArrayList<Long>();
        for (ContentReceive receive : receiveList) {
            int dstUserId = receive.getDstUserId();
            int srcUserId = receive.getSrcUserId();
            long timestamp = receive.getTimestamp();
            boolean b1 = timestamp >= start && timestamp <= end;
            boolean b2 = (userId1 == srcUserId && userId2 == dstUserId) || (userId1 == dstUserId && userId2 == srcUserId);
            if (b1 && b2) {
                receiveTimeStampList.add(timestamp);
            }
        }
        return receiveTimeStampList;
    }

    public static void main(String[] args) {
        SocialReciprocityCalculator calculator = new SocialReciprocityCalculator(DataParseUtils.getRequestFromCsv(),
                DataParseUtils.getTransmissionFromCsv(), DataParseUtils.getReceiveFromCsv());
        int fromUserId = RandomUtils.getRandomInterval(1, 75);
        int toUserId = RandomUtils.getRandomInterval(1, 75);
        while (fromUserId == toUserId) {
            fromUserId = RandomUtils.getRandomInterval(1, 75);
        }
        double averageMutualAidInterval = calculator.getAverageMutualAidInterval(fromUserId, toUserId, RandomUtils.getRandom(1000), System.currentTimeMillis());
        log.info("average mutual aid interval between {} and {} is {}ms.", fromUserId, toUserId, averageMutualAidInterval);
    }
}

