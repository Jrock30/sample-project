package com.sample.project.common.utils;

import com.sample.project.common.exception.DefaultException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 스트링 유틸
 */
@Slf4j
public class StringUtils extends org.springframework.util.StringUtils {

    public static final String EMPTY_STRING = "";
    public static final String BLANK = " ";

    private static final Random random = new Random();

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isNotEmpty(String param) {
        return !ObjectUtils.isEmpty(param);
    }

    public static boolean isEmpty(String param) {
        return ObjectUtils.isEmpty(param);
    }

    public static String getSearchStartDt(String startDt) {
        return changeEmpty(startDt, "0000-01-01");
    }

    public static String getSearchEndDt(String startDt) {
        return changeEmpty(startDt, "9999-12-31");
    }

    public static String changeEmpty(String param, String def) {
        if (hasText(param)) {
            return param;
        } else {
            return def;
        }
    }

    public static String leftPad(String s, int n) {
        if (ObjectUtils.isEmpty(s)) {
            s = EMPTY_STRING;
        }
        return String.format("%" + n + "s", s);
    }

    public static String rightPad(String s, int n) {
        if (ObjectUtils.isEmpty(s)) {
            s = EMPTY_STRING;
        }
        return String.format("%-" + n + "s", s);
    }

    /**
     * size만큼 랜덤 문자열 생성
     *
     * @param size
     * @return
     */
    public static String getRandomAlphabetic(int size) {
        if (size < 1) {
            size = 1;
        }
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = size;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * size만큼 랜덤 문자열+숫자 생성
     *
     * @param size
     * @return
     */
    public static String getRandomAlphanumeric(int size) {
        if (size < 1) {
            size = 1;
        }
        int leftLimit = 48; // letter '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = size;

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Tenth2 저장용 파일명 새성
     *
     * @return
     */
    public static String getUuidAndRandomAlphanumeric() {
        return delete(UUID.randomUUID().toString(), "-") + getRandomAlphanumeric(4);
    }

    /**
     * 문자열 인코딩을 고려해서 문자열 자르기
     * - 잘리는 캐릭터는 버림
     *
     * @param str
     * @param byteLength
     * @return
     */
    public static String getByteSubstr(String str, int byteLength) {
        Charset utf8Charset = StandardCharsets.UTF_8;
        CharsetDecoder cd = utf8Charset.newDecoder();

        byte[] sba = str.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = ByteBuffer.wrap(sba, 0, byteLength); // len in [B]
        CharBuffer cb = CharBuffer.allocate(byteLength);
        cd.onMalformedInput(CodingErrorAction.IGNORE);
        cd.decode(bb, cb, true);
        cd.flush(cb);

        return new String(cb.array(), 0, cb.position());
    }

    /**
     * 문자열 인코딩에 따라서 글자수 체크
     *
     * @param sequence
     * @return
     */
    public static int getByteLength(CharSequence sequence) {
        int count = 0;

        for (int i = 0, len = sequence.length(); i < len; i++) {
            char ch = sequence.charAt(i);

            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }

        return count;
    }

    public static Object[] convertReadValue(String data, Class<?> s) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return (Object[]) objectMapper.readValue(data, s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * ObjectMapper로 JSON 문자열을 Parsing한다.
     * JSON 파싱에 실패한다면 null 반환.
     * @param data
     * @param s
     * @param <T>
     * @return
     */
    public static <T> T convertReadValue(String data, TypeReference<T> s) {
        if (StringUtils.isNotEmpty(data)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(data, s);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 결과값 json string으로 변환
     *
     * @param t
     * @return
     */
    public static String convertJsonString(Object t) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * 문자열 값을 split 한 뒤, 특정 숫자 클래스로 변환하여 반환하는 유틸
     *
     * @param str
     * @param splitCharacter
     * @param clazz
     * @param <T>
     * @return
     */
    public static List<Long> splitAndToLong(String str, String splitCharacter) {
        return Arrays.stream(str.split(splitCharacter))
                .map(String::valueOf)
                .map(Long::parseLong)
                .collect(Collectors.toList())
                ;
    }

    /*
     * StringBuilder replace Util
     * @param targetStr
     * @param replaceStr
     * @param sourceBuilder
     * */
    public static void replace(String target, Object obj, StringBuilder builder) {
        int indexOfTarget = -1;
        while ((indexOfTarget = builder.indexOf(target)) > 0) {
            builder.replace(indexOfTarget, indexOfTarget + target.length(),
                    String.valueOf(obj));
        }

    }

    /*
     * String UTF-8 encoding
     * */
    public static String encodeString(String filename) throws DefaultException {
        try {

            return URLEncoder.encode(filename, "UTF-8");
        } catch (Exception e) {
            throw new DefaultException("잘못된 요청");
        }
    }


    /*
     * 문자열을 BigDecimal로 형변환한다.
     *
     * @param src
     * @return
     */
    public static BigDecimal parseBigDecimal(String src) {
        try {
            if (src == null || "".equals(src)) {
                return new BigDecimal(0);
            }
            return new BigDecimal(src.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0);
        }
    }
}
