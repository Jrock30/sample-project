package com.sample.project.api.access.utils;

import com.sample.project.common.exception.CustomException;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Slf4j
@Component
public class Apple24Validator {

    @Value("${apple24.secret-key}")
    private String apple24SecretKey;

    /**
     * @author : user
     * @param  : String
     * @param  : hmac_cafe
     * @desc   : timestamp 검증 : Replay Attack방지를 위해서 생성된 시간부터 일정 시간이 지난 호출의 경우 요청 무효 처리(필수)
     * @since  : 2022/11/04
     */
    public boolean timestampValidate(String timestamp) {
        long time = new Date().getTime() / 1000;
        long diffHour = (time - Integer.parseInt(timestamp)) / 3600;

        // ±2 시간 이내의 요청만 허용
        return Math.abs(diffHour) < 2;
    }

  
    public boolean hmacValidate(String queryString, String enHmac) {
        String secretKey = apple24SecretKey;
        String madeHmac;
        String hmac = enHmac;
        String plain_query = queryString.substring(0, queryString.lastIndexOf("&"));

        log.debug("plain_query >>> {}", plain_query);

        if (hmac.contains("%")) {
            hmac = URLDecoder.decode(enHmac, StandardCharsets.UTF_8);
        }

        if (!plain_query.contains("%")) {
            plain_query = URLEncoder.encode(plain_query, StandardCharsets.UTF_8);
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
            mac.update(plain_query.getBytes(StandardCharsets.UTF_8));
            madeHmac = org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(mac.doFinal());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.debug(CommonUtils.getPrintStackTrace(e));
            throw new CustomException(ResponseErrorCode.FAIL_500.message(), ResponseErrorCode.FAIL_500.status());
        }

        log.debug("hmac >>     {}", hmac);
        log.debug("madeHmac >> {}", madeHmac);
        return !hmac.equals(madeHmac);
    }
}
