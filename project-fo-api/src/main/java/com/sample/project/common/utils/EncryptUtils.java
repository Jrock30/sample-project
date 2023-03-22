package com.sample.project.common.utils;

import com.sample.project.common.exception.CustomException;
import com.sample.project.common.type.ResponseErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class EncryptUtils {

    /**
     * @author : user
     * @param  : String
     * @desc   : 비밀번호 SHA-512 해시 변환
     * @since  : 2022/11/04
     */
    public static String passwordEncrypt(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(input.getBytes("utf8"));
            return String.format("%128x", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new CustomException(ResponseErrorCode.FAIL_500.message(), ResponseErrorCode.FAIL_500.status());
        }
    }
}
