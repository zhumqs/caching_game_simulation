package com.zhumqs.util;

import com.alibaba.fastjson.JSONObject;
import com.zhumqs.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Slf4j
@Data
public class DataParseUtils {
    public static List<Activity> activities = new ArrayList<Activity>();
    public static List<Message> messages = new ArrayList<Message>();
    public static List<Reception> receptions = new ArrayList<Reception>();
    public static List<Transmission> transmissions = new ArrayList<Transmission>();
    public static List<Proximity> proximities = new ArrayList<Proximity>();
    public static final String PATTERN = "(?<=\\()[^\\)]+";

    public static List<MobileUser> getMobileUsersFromCsv() {
        URL url = Content.class.getClassLoader().getResource("mobile_user.csv");
        if (url == null) {
            log.error("mobile_user.csv not exist!");
            return null;
        }
        List<MobileUser> users = new ArrayList<MobileUser>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String header = "";
            if (reader.ready()) {
                header = reader.readLine();
            }
            String[] headers = header.split(";");
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    MobileUser user = new MobileUser();
                    //userId;institute;city;country;longitude;latitude
                    for (String field : headers) {
                        if ("userId".equals(field)) {
                            user.setUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("institute".equals(field)) {
                            user.setInstitute(Integer.valueOf(st.nextToken().trim()));
                        } else if ("city".equals(field)) {
                            user.setCity(Integer.valueOf(st.nextToken().trim()));
                        } else if ("country".equals(field)) {
                            user.setCountry(Integer.valueOf(st.nextToken().trim()));
                        } else if ("latitude".equals(field)){
                            user.setLatitude(Double.valueOf(st.nextToken().trim()));
                        } else {
                            user.setLongitude(Double.valueOf(st.nextToken().trim()));
                        }
                    }
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<ContentRequest> getRequestFromCsv() {
        URL url = Content.class.getClassLoader().getResource("content_request.csv");
        if (url == null) {
            log.error("content_request.csv not exist!");
            return null;
        }
        List<ContentRequest> requests = new ArrayList<ContentRequest>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String header = "";
            if (reader.ready()) {
                header = reader.readLine();
            }
            String[] headers = header.split(";");
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    ContentRequest request = new ContentRequest();
                    // requestUserId;type;contentId;dstUserId;created
                    for (String field : headers) {
                        if ("requestUserId".equals(field)) {
                            request.setRequestUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("type".equals(field)) {
                            request.setType(Integer.valueOf(st.nextToken().trim()));
                        } else if ("contentId".equals(field)) {
                            request.setContentId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("dstUserId".equals(field)) {
                            request.setDstUserId(Integer.valueOf(st.nextToken().trim()));
                        } else {
                            request.setCreated(Long.valueOf(st.nextToken().trim()));
                        }
                    }
                    requests.add(request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public static List<ContentTransmission> getTransmissionFromCsv() {
        URL url = Content.class.getClassLoader().getResource("content_transmission.csv");
        if (url == null) {
            log.error("content_transmission.csv not exist!");
            return null;
        }
        List<ContentTransmission> transmissions = new ArrayList<ContentTransmission>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String header = "";
            if (reader.ready()) {
                header = reader.readLine();
            }
            String[] headers = header.split(";");
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    ContentTransmission transmission = new ContentTransmission();
                    // contentId;bytes;srcUserId;dstUserId;type;timestamp
                    for (String field : headers) {
                        if ("srcUserId".equals(field)) {
                            transmission.setSrcUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("type".equals(field)) {
                            transmission.setType(Integer.valueOf(st.nextToken().trim()));
                        } else if ("contentId".equals(field)) {
                            transmission.setContentId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("dstUserId".equals(field)) {
                            transmission.setDstUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("timestamp".equals(field)){
                            transmission.setTimestamp(Long.valueOf(st.nextToken().trim()));
                        } else {
                            transmission.setBytes(Integer.valueOf(st.nextToken().trim()));
                        }
                    }
                    transmissions.add(transmission);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transmissions;
    }

    public static List<ContentReceive> getReceiveFromCsv() {
        URL url = Content.class.getClassLoader().getResource("content_receive.csv");
        if (url == null) {
            log.error("content_receive.csv not exist!");
            return null;
        }
        List<ContentReceive> receives = new ArrayList<ContentReceive>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String header = "";
            if (reader.ready()) {
                header = reader.readLine();
            }
            String[] headers = header.split(";");
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    ContentReceive receive = new ContentReceive();
                    // contentId;dstUserId;srcUserId;type;timestamp
                    for (String field : headers) {
                        if ("srcUserId".equals(field)) {
                            receive.setSrcUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("dstUserId".equals(field)) {
                            receive.setDstUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("timestamp".equals(field)){
                            receive.setTimestamp(Long.valueOf(st.nextToken().trim()));
                        } else if ("type".equals(field)) {
                            receive.setType(Integer.valueOf(st.nextToken().trim()));
                        } else {
                            receive.setContentId(Integer.valueOf(st.nextToken().trim()));
                        }
                    }
                    receives.add(receive);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receives;
    }

    public static List<Content> getContentsFromCsv() {
        URL url = Content.class.getClassLoader().getResource("content.csv");
        if (url == null) {
            log.error("content.csv not exist!");
            return null;
        }
        List<Content> contents = new ArrayList<Content>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("contentId")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Content content = new Content();
                    content.setContentId(Integer.valueOf(st.nextToken().trim()));
                    String themes = st.nextToken().trim().replace("[", "").replace("]", "");
                    String[] arr = themes.split(",");
                    List<Integer> themeList = new ArrayList<Integer>();
                    for (String s : arr) {
                        themeList.add(Integer.valueOf(s.trim()));
                    }
                    content.setThemeList(themeList);
                    content.setSize(Integer.valueOf(st.nextToken().trim()));
                    contents.add(content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static List<TrustRecord> getTrustRecordFromCsv() {
        URL url = Content.class.getClassLoader().getResource("trust_record.csv");
        if (url == null) {
            log.error("trust_record.csv not exist!");
            return null;
        }
        List<TrustRecord> records = new ArrayList<>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String header = "";
            if (reader.ready()) {
                header = reader.readLine();
            }
            String[] headers = header.split(";");
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    TrustRecord record = new TrustRecord();
                    // fromUserId;toUserId;values
                    for (String field : headers) {
                        if ("fromUserId".equals(field)) {
                            record.setFromUserId(Integer.valueOf(st.nextToken().trim()));
                        } else if ("toUserId".equals(field)) {
                            record.setToUserId(Integer.valueOf(st.nextToken().trim()));
                        } else {
                            String content = st.nextToken().trim();
                            List<TrustRecord.TrustValue> values = new ArrayList<>();
                            Pattern pattern = Pattern.compile(PATTERN);
                            Matcher matcher = pattern.matcher(content);
                            while(matcher.find()){
                                TrustRecord.TrustValue value = new TrustRecord.TrustValue();
                                String[] kvArr = matcher.group().split(",");
                                // preferenceSimilarity,cooperativeCapacity,socialReciprocity,decision,priorProbability,timestamp
                                for (String s : kvArr) {
                                    String[] kv = s.trim().split("=");
                                    String k = kv[0], v = kv[1];
                                    if ("preferenceSimilarity".equals(k)){
                                        value.setPreferenceSimilarity(Double.valueOf(v));
                                    } else if ("cooperativeCapacity".equals(k)) {
                                        value.setCooperativeCapacity(Double.valueOf(v));
                                    } else if ("socialReciprocity".equals(k)){
                                        value.setSocialReciprocity(Double.valueOf(v));
                                    } else if ("decision".equals(k)) {
                                        value.setDecision(Integer.valueOf(v));
                                    } else if ("priorProbability".equals(k)) {
                                        value.setPriorProbability(Double.valueOf(v));
                                    } else {
                                        value.setTimestamp(Long.valueOf(v));
                                    }
                                }
                                values.add(value);
                            }
                            record.setValues(values);
                        }
                    }
                    records.add(record);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static List<MobileUser> getParticipantsFromCsv() {
        URL url = Content.class.getClassLoader().getResource("participants.csv");
        if (url == null) {
            log.error("participants.csv not exist!");
            return null;
        }
        List<MobileUser> users = new ArrayList<MobileUser>();
        try {
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            MobileUser user = new MobileUser();
            while (reader.ready()) {
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    int userId = Integer.valueOf(st.nextToken().trim());
                    String key = st.nextToken().trim();
                    int value = Integer.valueOf(st.nextToken().trim());
                    if ("insitute".equals(key)) {
                        if (userId != 1) {
                            user = new MobileUser();
                        }
                        user.setUserId(userId);
                        user.setInstitute(value);
                    } else if ("city".equals(key)) {
                        user.setCity(value);
                    } else {
                        user.setCountry(value);
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void parseCsvOld() {
        try {

            // 1. activity.csv
            URL url = Content.class.getClassLoader().getResource("activity.csv");
            if (url == null) {
                log.error("activity.csv not exist!");
                return;
            }
            String readPath = url.getPath();
            File inFile = new File(readPath);
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Activity activity = new Activity();
                    activity.setUserId(Integer.valueOf(st.nextToken().trim()));
                    activity.setStart(Integer.valueOf(st.nextToken().trim()));
                    activity.setEnd(Integer.valueOf(st.nextToken().trim()));
                    activities.add(activity);
                }
            }
            reader.close();

            // 2. messages.csv
            url = Content.class.getClassLoader().getResource("messages.csv");
            if (url == null) {
                log.error("messages.csv not exist!");
                return;
            }
            readPath = url.getPath();
            inFile = new File(readPath);
            reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Message message = new Message();
                    message.setMsgId(Integer.valueOf(st.nextToken().trim()));
                    message.setSrcUserId(Integer.valueOf(st.nextToken().trim()));
                    message.setCreated(Integer.valueOf(st.nextToken().trim()));
                    message.setType(st.nextToken().trim());
                    message.setDst(Integer.valueOf(st.nextToken().trim()));
                    messages.add(message);
                }
            }
            reader.close();

            // 3. reception.csv
            url = Content.class.getClassLoader().getResource("reception.csv");
            if (url == null) {
                log.error("reception.csv not exist!");
                return;
            }
            readPath = url.getPath();
            inFile = new File(readPath);
            reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Reception reception = new Reception();
                    reception.setType(Integer.valueOf(st.nextToken().trim()));
                    reception.setMsgId(Integer.valueOf(st.nextToken().trim()));
                    reception.setHopSrcUserId(Integer.valueOf(st.nextToken().trim()));
                    reception.setHopDstUserId(Integer.valueOf(st.nextToken().trim()));
                    reception.setDstTimestamp(Integer.valueOf(st.nextToken().trim()));
                    receptions.add(reception);
                }
            }
            reader.close();

            // 4. proximity.csv
            url = Content.class.getClassLoader().getResource("proximity.csv");
            if (url == null) {
                log.error("reception.csv not exist!");
                return;
            }
            readPath = url.getPath();
            inFile = new File(readPath);
            reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Proximity proximity = new Proximity();
                    proximity.setTimestamp(Integer.valueOf(st.nextToken().trim()));
                    proximity.setUserId(Integer.valueOf(st.nextToken().trim()));
                    proximity.setSeenUserId(Integer.valueOf(st.nextToken().trim()));
                    proximity.setSeenDeviceMajorCod(Integer.valueOf(st.nextToken().trim()));
                    proximity.setSeenDeviceMinorCod(Integer.valueOf(st.nextToken().trim()));
                    proximities.add(proximity);
                }
            }
            reader.close();

            // 5. transmission.csv
            url = Content.class.getClassLoader().getResource("transmission.csv");
            if (url == null) {
                log.error("transmission.csv not exist!");
                return;
            }
            readPath = url.getPath();
            inFile = new File(readPath);
            reader = new BufferedReader(new FileReader(inFile));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.hasMoreTokens()) {
                    Transmission transmission = new Transmission();
                    transmission.setType(Integer.valueOf(st.nextToken().trim()));
                    transmission.setMsgId(Integer.valueOf(st.nextToken().trim()));
                    transmission.setBytes(Integer.valueOf(st.nextToken().trim()));
                    transmission.setHopSrcUserId(Integer.valueOf(st.nextToken().trim()));
                    transmission.setHopDstUserId(Integer.valueOf(st.nextToken().trim()));
                    transmission.setSrcTimestamp(Integer.valueOf(st.nextToken().trim()));
                    transmission.setStatus(Integer.valueOf(st.nextToken().trim()));
                    transmissions.add(transmission);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //DataParseUtils.parseCsvOld();
        List<ContentRequest> requests = DataParseUtils.getRequestFromCsv();
        log.info(JSONObject.toJSONString(requests));

        List<ContentTransmission> transmissions = DataParseUtils.getTransmissionFromCsv();
        log.info(JSONObject.toJSONString(transmissions));

        List<ContentReceive> receives = DataParseUtils.getReceiveFromCsv();
        log.info(JSONObject.toJSONString(receives));

        List<MobileUser> users = DataParseUtils.getParticipantsFromCsv();
        log.info(JSONObject.toJSONString(users));

        List<TrustRecord> records = DataParseUtils.getTrustRecordFromCsv();
        log.info(JSONObject.toJSONString(records));

        List<MobileUser> mobileUsers = DataParseUtils.getMobileUsersFromCsv();
        log.info(JSONObject.toJSONString(mobileUsers));
     }

}
