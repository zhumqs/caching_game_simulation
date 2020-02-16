package com.zhumqs.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

import com.csvreader.CsvWriter;
import com.zhumqs.constants.ExperimentConstants;
import com.zhumqs.model.Content;
import com.zhumqs.model.MobileUser;
import com.zhumqs.model.TrustRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Slf4j
public class CsvUtils {

    /**
     *  Description: 将List<T>类型数据以csv存储至本地
     *  list：list<T>
     *  csvFilePath： 如D:/XXX/DATA20190821.csv
     *  csvHeaders：表头
     */
    public static <T> void writeCsv(Collection<T> list,String csvFilePath) {
        try {
            // 定义路径，分隔符，编码
            CsvWriter csvWriter = new CsvWriter(csvFilePath, ';',  Charset.forName("UTF-8"));
            // 写表头
            for (T t : list) {
                Field[] fields = t.getClass().getDeclaredFields();
                String[] csvHeaders = new String[fields.length];
                for (short i = 0; i < fields.length; i++) {
                    csvHeaders[i] = fields[i].getName();
                }
                csvWriter.writeRecord(csvHeaders);
                break;
            }
            // 写内容
            for (T t : list) {
                //获取类属性
                Field[] fields = t.getClass().getDeclaredFields();
                String[] csvContent = new String[fields.length];
                for (short i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get"
                            + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName);
                        Object value = getMethod.invoke(t);
                        if (value == null) {
                            continue;
                        }
                        //取值并赋给数组
                        String textvalue = value.toString();
                        csvContent[i] = textvalue;
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }

                //迭代插入记录
                csvWriter.writeRecord(csvContent);
            }
            csvWriter.close();
            log.info("<--------CSV文件写入成功-------->");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Content> mockContent() {
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
            content.setSize(ExperimentConstants.CONTEN_DEFAULT_SIZE);
            contents.add(content);
        }
        return contents;
    }

    private static List<TrustRecord> mockTrustRecord() {
        List<TrustRecord> mockRecords = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            for (int j = 1; j <= 75; j++) {
                TrustRecord record = new TrustRecord();
                record.setFromUserId(i);
                record.setToUserId(j);
                List<TrustRecord.TrustValue> values = new ArrayList<>();
                TrustRecord.TrustValue value = new TrustRecord.TrustValue();
                // 0: unreliable 1: reliable 2: observed
                value.setDecision(RandomUtils.getRandom(3));
                value.setPriorProbability(0.5);
                value.setTimestamp(System.currentTimeMillis() - 5 * 60 * 60 * 1000);
                value.setSocialReciprocity(0);
                value.setCooperativeCapacity(0);
                value.setPreferenceSimilarity(0);
                values.add(value);

                TrustRecord.TrustValue value1 = new TrustRecord.TrustValue();
                // 0: unreliable 1: reliable 2: observed
                value1.setDecision(RandomUtils.getRandom(3));
                value1.setPriorProbability(0.5);
                value1.setTimestamp(System.currentTimeMillis() - 5 * 60 * 60 * 1000);
                value1.setSocialReciprocity(0);
                value1.setPreferenceSimilarity(0);
                value1.setCooperativeCapacity(0);
                values.add(value1);

                record.setValues(values);
                mockRecords.add(record);
            }
        }
        return mockRecords;
    }

    // 32.114967(经度:Longitude),118.928547(纬度:Latitude)
    private static List<MobileUser> mockMobileUsers() {
        List<MobileUser> users = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            MobileUser user = new MobileUser();
            user.setUserId(i);
            user.setCity(RandomUtils.getRandomInterval(1, 20));
            user.setInstitute(RandomUtils.getRandomInterval(1, 30));
            user.setCountry(RandomUtils.getRandomInterval(1, 20));
            user.setLongitude(32.114967 + RandomUtils.getRandomInterval(10, 800) * 0.000001 * RandomUtils.getPlusOrMinus());
            user.setLatitude(118.928547 + RandomUtils.getRandomInterval(10, 800) * 0.000001 * RandomUtils.getPlusOrMinus());
            users.add(user);
        }
        return users;
    }

    public static void main(String[] args) {
        //String csvFileName = "content.csv";
        //String csvPath = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName;
        //log.info(csvPath);
        //writeCsv(mockContent(), csvPath);

        //String csvFileName1 = "trust_record.csv";
        //String csvPath1 = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName1;
        //log.info(csvPath1);
        //writeCsv(mockTrustRecord(), csvPath1);

        String csvFileName1 = "mobile_user.csv";
        String csvPath1 = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName1;
        log.info(csvPath1);
        writeCsv(mockMobileUsers(), csvPath1);
    }

}

