package com.zhumqs.thread;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.util.CsvUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Slf4j
public class Executor {

    /**
     * 1. 创建线程工厂
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("simulation-pool-%d")
            .build();
    /**
     * 2. 自定义线程池
     * 参数含义：
     *      corePoolSize : 线程池中常驻的线程数量。核心线程数，默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。
     *      maximumPoolSize : 线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。
     *      keepAliveTime : 当线程数量多于corePoolSize时，空闲线程的存活时长，超过这个时间就会被回收
     *      unit : keepAliveTime的时间单位
     *      workQueue : 存放待处理任务的队列，该队列只接收Runnable接口
     *      threadFactory : 线程创建工厂
     *      handler : 当线程池中的资源已经全部耗尽，添加新线程被拒绝时，会调用RejectedExecutionHandler的rejectedExecution方法，参考 ThreadPoolExecutor 类中的内部策略类
     */
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(76, 200, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            threadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {

        List<Resource> resourceList = new ArrayList<Resource>();
        for (int i = 0; i < 10; i++) {
            resourceList.add(new Resource());
        }

        threadPoolExecutor.submit(new Printer());
        for (int i = 0; i < 75; i++) {
            Resource res = resourceList.get(new Random().nextInt(10));
            threadPoolExecutor.submit(new Requestor(res));
            threadPoolExecutor.submit(new Receiver(res));
        }

        threadPoolExecutor.shutdown();
    }

    public static class Printer implements Runnable {

        @Override
        public void run() {
            while (true) {
                // 五分钟更新一次csv文件
                try {
                    String requestCsvPath = ExperimentConstants.CSV_DIRECTORY + "/" + "content_request.csv";
                    String transmissionCsvPath = ExperimentConstants.CSV_DIRECTORY + "/" + "content_transmission.csv";
                    String receiveCsvPath = ExperimentConstants.CSV_DIRECTORY + "/" + "content_receive.csv";
                    log.info(JSONObject.toJSONString(Resource.requestList));
                    CsvUtils.writeCsv(Resource.requestList, requestCsvPath);
                    log.info(JSONObject.toJSONString(Resource.transmissionList));
                    CsvUtils.writeCsv(Resource.transmissionList, transmissionCsvPath);
                    log.info(JSONObject.toJSONString(Resource.receiveList));
                    CsvUtils.writeCsv(Resource.receiveList, receiveCsvPath);
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
