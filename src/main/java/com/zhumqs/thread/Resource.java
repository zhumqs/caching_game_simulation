package com.zhumqs.thread;

import com.zhumqs.model.ContentReceive;
import com.zhumqs.model.ContentRequest;
import com.zhumqs.model.ContentTransmission;
import com.zhumqs.util.RandomUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Slf4j
@Data
public class Resource {
    private int contentId;
    private int requestUserId;
    private int type;
    private int dstUserId;
    private boolean flag = false;
    private final Lock lock = new ReentrantLock();
    private Condition conditionRequest = lock.newCondition();
    private Condition conditionReceive = lock.newCondition();
    // Collections.synchronizedList(new ArrayList<>())仍会发生并发修改异常, 因此改为写时复制容器CopyOnWriteArrayList
    public static List<ContentRequest> requestList = new CopyOnWriteArrayList<ContentRequest>(new ArrayList<ContentRequest>());
    public static List<ContentTransmission> transmissionList = new CopyOnWriteArrayList<ContentTransmission>(new ArrayList<ContentTransmission>());
    public static List<ContentReceive> receiveList = new CopyOnWriteArrayList<ContentReceive>(new ArrayList<ContentReceive>());

    void request(int contentId) {
        lock.lock();
        try {
            //关键点 此处一定要用while循环
            while (flag) {
                try {
                    conditionRequest.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // request
            ContentRequest request = new ContentRequest();
            Integer requestUserId = RandomUtils.getRandomInterval(1, 75);
            request.setRequestUserId(requestUserId);
            int type = new Random().nextInt(2);
            if (type == 0) {
                int dstUserId = RandomUtils.getRandomInterval(1, 75);
                while (dstUserId == requestUserId) {
                    dstUserId = RandomUtils.getRandomInterval(1, 75);
                }
                request.setDstUserId(dstUserId);
                request.setType(0);
                this.dstUserId = dstUserId;
                log.info("D2D用户{}向D2D用户{}请求内容{}", requestUserId, dstUserId, contentId);
            } else {
                request.setDstUserId(0);
                request.setType(1);
                log.info("D2D用户{}向BS请求内容{}", requestUserId, contentId);
            }
            request.setCreated(System.currentTimeMillis());
            request.setContentId(contentId);
            requestList.add(request);

            this.contentId = contentId;
            this.type = type;
            this.requestUserId = requestUserId;
            Thread.sleep(new Random().nextInt(500));
            flag = true;
            conditionReceive.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void receive() {
        lock.lock();
        try {
            while (!flag) {
                try {
                    conditionReceive.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // transmission
            ContentTransmission transmission = new ContentTransmission();
            transmission.setBytes(1024);
            transmission.setContentId(this.contentId);
            transmission.setDstUserId(this.requestUserId);
            long transmissionTime = System.currentTimeMillis();
            transmission.setTimestamp(transmissionTime);
            transmission.setType(this.type);
            // 为1说明基站发送则srcUserId为0
            if (this.type == 0) {
                // 决定是否进行内容发送
                int decision = new Random().nextInt(11);
                if (decision > 7) {
                    log.info("D2D用户{}接受到D2D用户{}请求内容{},不发送内容!", this.dstUserId, this.requestUserId, this.contentId);
                } else {
                    transmission.setSrcUserId(this.dstUserId);
                    transmissionList.add(transmission);
                    log.info("D2D用户{}接受到D2D用户{}请求内容{},发送内容!", this.dstUserId, this.requestUserId, this.contentId);

                    // receive
                    ContentReceive receive = new ContentReceive();
                    receive.setContentId(this.contentId);
                    receive.setDstUserId(this.requestUserId);
                    receive.setTimestamp(transmissionTime + RandomUtils.getRandomInterval(500, 1800));
                    receive.setType(this.type);
                    receive.setSrcUserId(this.dstUserId);
                    receiveList.add(receive);
                    log.info("D2D用户{}成功接收到D2D用户{}发送的内容{}", this.requestUserId, this.dstUserId, this.contentId);
                }
            } else {
                transmission.setSrcUserId(0);
                transmissionList.add(transmission);
                log.info("BS接受到D2D用户{}请求内容{},发送内容!", this.requestUserId, this.contentId);

                // receive
                ContentReceive receive = new ContentReceive();
                receive.setContentId(this.contentId);
                receive.setDstUserId(this.requestUserId);
                receive.setTimestamp(transmissionTime + RandomUtils.getRandomInterval(200, 1500));
                receive.setType(this.type);
                receive.setSrcUserId(0);
                receiveList.add(receive);
                log.info("D2D用户{}成功接收到BS发送的内容{}", this.requestUserId, this.contentId);
            }

            flag = false;
            conditionRequest.signal();
        } finally {
            lock.unlock();
        }
    }
}
