package com.sample.project.common.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class EncryptUtilsTest {

    @DisplayName("Apple24 HMAC 검증 테스트")
    @Test
    public void hmacValidatorTest() throws UnsupportedEncodingException {
        String made_hamc = "";
//        String query_string = "is_multi_shop=T&lang=ko_KR&mall_id=alisonjung&nation=KR&shop_no=1&timestamp=1668572412&user_id=alisonjung&user_name=정유경&user_type=P&hmac=s4biI8DP0J7qnrySQ8bdhsNQtwRwVlXr3HynbONBmko%3D";
        String query_string = "lang=ko_KR&mall_id=alisonjung&nation=KR&shop_no=1&timestamp=1668574426&user_id=alisonjung&user_name=정유경&user_type=P&hmac=trUl2O0hFxyseY6oQAtHfQXb4sq%2BpPyqRkokVR4xeKE%3D";

//        lang=ko_KR
//        mall_id=alisonjung
//        nation=KR
//        shop_no=1
//        timestamp=1668572412
//        user_id=alisonjung
//        user_name=정유경
//        user_type=P
//        hmac=s4biI8DP0J7qnrySQ8bdhsNQtwRwVlXr3HynbONBmko%3D"
//        lang=ko_KR&mall_id=alisonjung&nation=KR&shop_no=1&timestamp=1668572412&user_id=alisonjung&user_name=정유경&user_type=P&hmac=s4biI8DP0J7qnrySQ8bdhsNQtwRwVlXr3HynbONBmko%3D
//        lang=ko_KR&mall_id=alisonjung&nation=KR&shop_no=1&timestamp=1668572412&user_id=alisonjung&user_name=정유경&user_type=P&hmac=s4biI8DP0J7qnrySQ8bdhsNQtwRwVlXr3HynbONBmko=

        // lang%3Dko_KR%26mall_id%3Dalisonjung%26nation%3DKR%26shop_no%3D1%26timestamp%3D1668572412%26user_id%3Dalisonjung%26user_name%3D%EC%A0%95%EC%9C%A0%EA%B2%BD%26user_type%3DP%26hmac%3Ds4biI8DP0J7qnrySQ8bdhsNQtwRwVlXr3HynbONBmko%253D

        String decode = URLDecoder.decode(query_string, "utf-8");

//        String decode = query_string;

        String plain_query = decode.substring(0, decode.lastIndexOf("&"));
        String hmac_cafe = decode.substring(decode.lastIndexOf("mac=") + 4);

//        String plain_query = query_string.substring(0, query_string.lastIndexOf("&"));
//        String hmac_cafe = query_string.substring(query_string.lastIndexOf("mac=") + 4);

//        String encode = URLEncoder.encode(plain_query, "utf-8");

//        String secretKey = "giEeznftgkwIif85vFUFGE"; // 엘리슨 관리자 테스트
//        String secretKey = "4zn6CVIBMFCvqLUhEwSItC"; // 엘리슨 code-issue
        String secretKey = "rASfHiUXDCeg2fhI11YgvC"; // jrock mall
//        String secretKey = "PeVeAbpuMEbvyDsl6ygs1O"; // 엘리슨 code-issue ( 클라이언트 ID )
//        String secretKey = "rASfHiUXDCeg2fhI11YgvC"; // 엘리슨 [B] 관리자테스트
        boolean hmacFlag = true;

        /**
         * TEST Data
         */
//         plain_query = URLDecoder.decode("is_multi_shop=T&lang=ko_KR&mall_id=jhbaek02&nation=KR&shop_no=1&timestamp=1622513360&user_id=jhbaek02&user_name=jhbaek02&user_type=P", "utf-8");
//         hmac_cafe = URLDecoder.decode("8%2BhYywQW5fBMpfbTlA1puAMpM91N0FYtrpzHYrdodDM%3D", "utf-8");
//         secretKey = "zoQxSUptApmiFLRl2ChaxB";


        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
            mac.update(plain_query.getBytes("UTF-8"));
            made_hamc = org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(mac.doFinal());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            hmacFlag = false;
        }

        if (!hmac_cafe.equals(made_hamc)) {
            hmacFlag = false;
//            throw new Exception("Authentication failed");
        }

        log.info("made_hamc >>> {}", made_hamc);
        log.info("hmac_cafe >>> {}", hmac_cafe);
        assertThat(hmacFlag).isTrue();

    }

    @DisplayName("현재 시간에서 2시간 경과 확인")
    @Test
    public void timestampValidatorTest() throws UnsupportedEncodingException {

        String query_string = "lang=ko_KR&mall_id=alisonjung&nation=KR&shop_no=1&timestamp=1668574426&user_id=alisonjung&user_name=정유경&user_type=P&hmac=trUl2O0hFxyseY6oQAtHfQXb4sq%2BpPyqRkokVR4xeKE%3D";

        String decode = URLDecoder.decode(query_string, "UTF-8");

        String timestamp = "1668581871";

        long time = new Date().getTime() / 1000;
        long diffHour = (time - Integer.parseInt(timestamp)) / 3600;

        log.debug("현재 시간 초 = {}", new Date().getTime() / 1000);

        // ±2 시간 이내의 요청만 허용
        boolean result = (Math.abs(diffHour) < 2);

        Assertions.assertThat(result).isTrue();

    }

    @Test
    void base64Test() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); //or HS384 or HS512
        String base64Key = Encoders.BASE64.encode(key.getEncoded());

        log.debug("key >> {}", key);
        log.debug("base64Key >>> {}", base64Key);
        // CmLtj0qt7t1XeI+XXr58LdO6SrsMTV2YHdIssCCPtFw=
    }

    @Test
    void 핸드폰번호_암호화_테스트() throws Exception {
        String number = "01033670450";

        String encrypt = AesUtils.encrypt(number);

        log.debug("encrypt >>> {}", encrypt);

        String decrypt = AesUtils.decrypt(encrypt);

        log.debug("decrypt >>> {}", decrypt);

        // 8Yu5ChkA9yWAbB5mNsx+Wg==
    }

}