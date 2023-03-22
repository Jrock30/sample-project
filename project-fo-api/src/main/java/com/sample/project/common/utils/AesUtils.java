package com.sample.project.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * 블록 암호화 유틸
 */
public class AesUtils {

    private static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_BLOCK_ALGO = "AES";
    private static final int DEFAULT_ITERATION_COUNT = 1024;
    private static final int DEFAULT_KEY_LENGTH = 128;

    private AesUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 블록암호화에 사용할 비밀키를 만들기 위한 기본 인증키
     */
    private static final String DEFAULT_AUTH_KEY = "project12#$";

    /**
     * 비밀키를 생성하는 데 쓰일 salt(32bytes)를 생성한다.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    @Deprecated
    private static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salts = new byte[32];
        sr.nextBytes(salts);
        return Base64.encodeBase64String(salts);
    }

    /**
     * PBKDF2 방식의 비밀키를 생성한다.
     *
     * @param authKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws DecoderException
     * @throws InvalidKeySpecException
     */
    private static String generateSecretKey(String authKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = StringUtils.rightPad(authKey, 32);

        return generateSecretKey(DEFAULT_ITERATION_COUNT, DEFAULT_KEY_LENGTH, authKey, salt);
    }

    /**
     * PBKDF2 방식의 비밀키를 생성한다.
     *
     * @param authKey
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws DecoderException
     * @throws InvalidKeySpecException
     */
    @Deprecated
    private static String generateSecretKey(String authKey, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generateSecretKey(DEFAULT_ITERATION_COUNT, DEFAULT_KEY_LENGTH, authKey, salt);
    }

    /**
     * PBKDF2 방식의 비밀키를 생성한다.
     *
     * @param iterationCount
     * @param keyLength
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws DecoderException
     * @throws InvalidKeySpecException
     */
    private static String generateSecretKey(int iterationCount, int keyLength, String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // System.out.println("===========================================================================================");
        // System.out.println(Base64.class.getProtectionDomain().getCodeSource().getLocation().toString());
        // System.out.println("===========================================================================================");

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterationCount, keyLength);
        // SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return Base64.encodeBase64String(secretKey.getEncoded());
    }

    /**
     * 암호화된 평문을 복호화한다.
     *
     * @param secretKey
     * @param encStr(Base64)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String decrypt(String secretKey, String encStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(secretKey, DEFAULT_BLOCK_ALGO, encStr);
    }

    /**
     * 암호화된 평문을 복호화한다.
     *
     * @param secretKey
     * @param algo
     * @param encStr(Base64)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String decrypt(String secretKey, String algo, String encStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String iv = secretKey.substring(0, 16);
        return decrypt(secretKey, algo, iv, encStr);
    }

    /**
     * 암호화된 평문을 복호화한다.
     *
     * @param secretKey
     * @param algo
     * @param iv(Hex)
     * @param encStr(Base64)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private static String decrypt(String secretKey, String algo, String iv, String encStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKey sk = new SecretKeySpec(Base64.decodeBase64(secretKey.getBytes()), algo);
        Cipher cipher = Cipher.getInstance(algo + "/CBC/PKCS5Padding");

        byte[] ivBytes = iv.getBytes();
        // byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

        cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(ivBytes));
        byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encStr.getBytes()));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 평문을 암호화한다.
     *
     * @param secretKey
     * @param plainText
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    private static String encrypt(String secretKey, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return encrypt(secretKey, DEFAULT_BLOCK_ALGO, plainText);
    }

    /**
     * 평문을 암호화한다.
     *
     * @param secretKey
     * @param algo
     * @param plainText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    private static String encrypt(String secretKey, String algo, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        SecretKey sk = new SecretKeySpec(Base64.decodeBase64(secretKey.getBytes()), algo);
        String iv = secretKey.substring(0, 16);
        Cipher c = Cipher.getInstance(algo + "/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(iv.getBytes()));

        byte[] encodedPlainText = new String(plainText).getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = c.doFinal(encodedPlainText);

        return Base64.encodeBase64String(encryptedBytes);
    }

    /**
     * 기본 비밀키로 평문을 암호화한다.
     *
     * @param plainText
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     */
    public static String encrypt(String plainText) throws Exception {
        return encrypt256(plainText);
//
//        String secretKey = generateSecretKey(DEFAULT_AUTH_KEY);
//        return encrypt(secretKey, sDEFAULT_BLOCK_ALGO, plainText);
    }

    /**
     * 기본 비밀키로 암호화된 평문을 복호화한다.
     *
     * @param encStr(Base64)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     */
    public static String decrypt(String encStr) throws Exception {
        return decrypt256(encStr);
//
//        String secretKey = generateSecretKey(DEFAULT_AUTH_KEY);
//        return decrypt(secretKey, DEFAULT_BLOCK_ALGO, encStr);
    }


    /**
     * 기본 비밀키로 암호화된 평문을 복호화한다.
     *
     * @param encStr(Base64)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     */
    public static String decryptPkg2AesDefault(String encStr) throws Exception {
        String secretKey = generateSecretKey(DEFAULT_AUTH_KEY);
        return decrypt(secretKey, DEFAULT_BLOCK_ALGO, encStr);
    }



    /* @formatter:off */
    /**
     public static void main(String[] args) throws Exception {
         String apiKey = StringUtils.getRandomAlphanumeric(32);
         System.out.println("apiKey: " + apiKey);
         String encApiKey = AesUtils.encrypt(apiKey);
         System.out.println(encApiKey);
         System.out.println("decApiKey: " + AesUtils.decrypt(encApiKey));

         String encHp = AesUtils.encrypt("01091990607");
         System.out.println(encHp);
     }
     */

    public static String encrypt128(String str) throws Exception {
        String secretKey128 = DEFAULT_AUTH_KEY.substring(0, 16);
        return encode(secretKey128, str);
    }

    public static String encrypt192(String str) throws Exception {
        String secretKey192 = StringUtils.rightPad(DEFAULT_AUTH_KEY, 24);
        return encode(secretKey192, str);
    }

    /*
     * AES 256 암호화
     * @param str
     * @return
     * @throws Exception
     */
    public static String encrypt256(String str) throws Exception {
        String secretKey256 = StringUtils.rightPad(DEFAULT_AUTH_KEY, 32);
        return encode(secretKey256, str);
    }

    /*
     * AES 암호화
     * @param secretKey
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String encode(String secretKey, String str) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyData = secretKey.getBytes();
        SecretKey secureKey = new SecretKeySpec(keyData, DEFAULT_BLOCK_ALGO);
        String iv = secretKey.substring(0, 16);

        Cipher c = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));

        byte[] temp = str.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = c.doFinal(temp);

        return new String(Base64.encodeBase64(encrypted));
    }

    public static String decrypt128(String encStr) throws Exception {
        String secretKey128 = DEFAULT_AUTH_KEY.substring(0, 16);
        return decode(secretKey128, encStr);
    }

    public static String decrypt192(String encStr) throws Exception {
        String secretKey192 = StringUtils.rightPad(DEFAULT_AUTH_KEY, 24);
        return decode(secretKey192, encStr);
    }

    /*
     * AES 256 복호화
     * @param encStr
     * @return
     * @throws Exception
     */
    public static String decrypt256(String encStr) throws Exception {
        String secretKey256 = StringUtils.rightPad(DEFAULT_AUTH_KEY, 32);
        return decode(secretKey256, encStr);
    }

    /*
     * AES 복호화
     * @param secretKey
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String decode(String secretKey, String str) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyData = secretKey.getBytes();
        SecretKey secureKey = new SecretKeySpec(keyData, DEFAULT_BLOCK_ALGO);
        String iv = secretKey.substring(0, 16);

        Cipher c = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

        byte[] temp = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        byte[] planText = c.doFinal(temp);

        return new String(planText, StandardCharsets.UTF_8);
    }

    /*
     * AES256으로 암호화된 데이터를 복호화하여 검색되게 해주는 Query.
     * 암호화된 데이터의 LIKE 검색이 필요할 때 사용된다.
     * DB 검색 속도나 보안적인 부분에 대한 고려가 필요.
     *
     * @param columnName
     * @return
     */
    public static String makeAes256DecryptColumnQuery(String columnName) {
        final String secretKey = StringUtils.rightPad(DEFAULT_AUTH_KEY, 32);
        return " CAST(AES_DECRYPT(FROM_BASE64(" + columnName + "), '" + secretKey + "', '" + secretKey + "') AS CHAR) ";
    }


}
