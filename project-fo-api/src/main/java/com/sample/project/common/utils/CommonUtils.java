package com.sample.project.common.utils;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author   	: user
 * @since    	: 2022/11/01
 * @desc     	: 공통 유틸
 */
@Slf4j
public class CommonUtils {

    /**
     * @author   	: user
     * @since    	: 2022/11/01
     * @desc     	: Object 객체를 받아 null일때 default value를 반환하는 공통함수
     */
    public static String strNlv(Object obj, String defaultVal) {
        if (obj == null || obj.equals("")) return defaultVal;
        else return obj.toString();
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/01
     * @desc     	: Exception 객체를 print stream으로 변환
     */
    public static String getPrintStackTrace(Object clsObject) {
        ByteArrayOutputStream clsOutStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(clsOutStream);
        ((Throwable) clsObject).printStackTrace(printStream);
        return clsOutStream.toString();
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/01
     * @desc     	: 클라이언트의 ip 얻기
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.length() > 15) ip = ip.substring(0, 15).trim();
        return ip;
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/01
     * @desc     	: 파일 확장자 체크(jpg, gif, png만 허용)
     */
    public static boolean checkFile(String fileName) {
        if (!fileName.toLowerCase().endsWith("jpg") && !fileName.toLowerCase().endsWith("png")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/01
     * @desc     	: java의 Long, Double, Float type은 64bit (10^19) 이며 javascript의 numeric type은 54bit (10^16)이기때문에 java의 numeric타입을 string type으로 변경함
     */
    public static List<Map<String, Object>> numericConvertListMapValue(List<Map<String, Object>> ListMap) throws Exception {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        try {
            for (Map<String, Object> map : ListMap) {
                Map<String, Object> returnMap = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof Long || entry.getValue() instanceof Float || entry.getValue() instanceof Double)
                        returnMap.put(entry.getKey(), entry.getValue().toString().trim());
                    else
                        returnMap.put(entry.getKey(), entry.getValue());
                }
                returnList.add(returnMap);
            }
            return returnList;
        } catch (Exception e) {
            //throw new SQLException(e.getMessage());
            //return returnList;
            return null;
        }
    }

    // 단순 Map 변환
    public static Map<String, Object> numericConvertMapValue(Map<String, Object> map) throws Exception {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Long || entry.getValue() instanceof Float || entry.getValue() instanceof Double)
                    returnMap.put(entry.getKey(), entry.getValue().toString().trim());
                else
                    returnMap.put(entry.getKey(), entry.getValue());
            }
            return returnMap;
        } catch (Exception e) {
            //throw new SQLException(e.getMessage());
            //return returnMap;
            return null;
        }
    }

    /**
     * aa******@naver.com 형식으로 변환
     */
    public static String maskingUserId(String strUserId) {
        String[] strip = strUserId.split("@");
        if (strip.length < 1) return strUserId;

        String maskStr = "";
        for (int i = 0; i < (strip[0].length() - 2); i++) {
            maskStr += "*";
        }
        String namePart = strip[0].substring(0, 2) + maskStr;
        //String urlPart = strip[1].substring(0, strip[1].length() - 4) + "****";
        return namePart + "@" + strip[1];
    }

    /**
     * aa******@applecorp**** 형식으로 변환
     */
    public static String maskingUserId2(String strUserId) {
        String[] strip = strUserId.split("@");
        if (strip.length < 1) return strUserId;

        String namePart = strip[0].substring(0, strip[0].length() - 3) + "***";
        String urlPart = strip[1].substring(0, strip[1].length() - 4) + "****";
        return namePart + "@" + urlPart;
    }


    /**
     * 핸드폰번호 masking 후 리턴
     * 변환 실패시 입력값 그대로 리턴
     * */
    public static String maskingPhoneNumber(String phoneNumber){
        try{
            if(ObjectUtils.isEmpty(phoneNumber)){
                return phoneNumber;
            }

            phoneNumber = phoneNumber.replaceAll("[^0-9]",""); // 숫자만 추출

            if(!(phoneNumber.length() == 10 || phoneNumber.length() == 11)){
                return phoneNumber;
            }

            if(phoneNumber.length() == 10){         // 10자리인 경우. 000-***-0000로 지환
                return phoneNumber.substring(0, 3) + "-***-" + phoneNumber.substring(6, 10);
            }else if(phoneNumber.length() == 11){   // 11자리인 경우. 000-****-0000로 지환
                return phoneNumber.substring(0, 3) + "-****-" + phoneNumber.substring(7, 11);
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return phoneNumber;
    }

    /**
     * 핸드폰번호 masking 후 리턴
     * 변환 실패시 입력값 그대로 리턴
     * */
    public static String formatPhoneNumber(String phoneNumber){
        try{
            if(ObjectUtils.isEmpty(phoneNumber)){
                return phoneNumber;
            }

            phoneNumber = phoneNumber.replaceAll("[^0-9]",""); // 숫자만 추출

            if(!(phoneNumber.length() == 10 || phoneNumber.length() == 11)){
                return phoneNumber;
            }

            if(phoneNumber.length() == 10){         // 10자리인 경우. 000-***-0000로 지환
                return phoneNumber.substring(0, 3) + phoneNumber.substring(3, 6) + phoneNumber.substring(6, 10);
            }else if(phoneNumber.length() == 11){   // 11자리인 경우. 000-****-0000로 지환
                return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7, 11);
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return phoneNumber;
    }


    /**
     * 포맷으로 오늘날짜를 리턴해준다.
     */
    public static String makeFormatToday(String format) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter;
        if (ObjectUtils.isEmpty(format)) {
//            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        } else {
            formatter = DateTimeFormatter.ofPattern(format);
        }
        return now.format(formatter);
//        return now;
    }

    /**
     * 오늘날짜 기준으로 day 날짜만큼 이전 날짜를 포맷에 맞추어 리턴해준다.
     */
    public static String makeMinusDate(String format, int day) {
        LocalDate now = LocalDate.now().minusDays(day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return now.format(formatter);
    }

    /**
     * Timestamp 날짜 비교
     */
    public static long compareToTime(LocalDateTime beforeTime, LocalDateTime afterTime) {
        long beforeTimestamp = Timestamp.valueOf(beforeTime).getTime();
        long afterTimestamp = Timestamp.valueOf(afterTime).getTime();
        return afterTimestamp - beforeTimestamp;
    }

    /**
     * Token 가져오기
     */
    public static String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return bearerToken.substring(7);
    }

    /**
     * QueryDsl BooleanBuilder null safe
     */
    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }


    /**
     * @author   	: user
     * @since    	: 2022/11/22
     * @desc     	: 날짜 유효성 체크
     */
    public static boolean isDate(LocalDate date, String fmt) {
        if (date == null) return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse( String.valueOf(date) );
            return true;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/22
     * @desc     	: 시간 유효성 체크
     */
    public static boolean isTime(LocalDateTime localDateTime){
        try{
            localDateTime.getHour();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

}
