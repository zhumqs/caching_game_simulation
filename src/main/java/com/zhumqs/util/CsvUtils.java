package com.zhumqs.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

import com.csvreader.CsvWriter;
import com.zhumqs.constants.ExperimentConstants;
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

    public static void main(String[] args) {
        String csvFileName = "content.csv";
        String csvPath = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName;
        log.info(csvPath);
        writeCsv(DataMockUtils.mockContent(), csvPath);

        String csvFileName1 = "mobile_user.csv";
        String csvPath1 = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName1;
        log.info(csvPath1);
        writeCsv(DataMockUtils.mockMobileUsers(), csvPath1);


        String csvFileName2 = "trust_record.csv";
        String csvPath2 = ExperimentConstants.CSV_DIRECTORY + "/" + csvFileName1;
        log.info(csvPath1);
        writeCsv(DataMockUtils.mockTrustRecord(), csvPath1);
    }
}

