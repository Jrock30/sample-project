package com.sample.project.common.service.storage;

import com.sample.project.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Tenth2StorageServiceImpl implements StorageService {

    @Value("${tenth2.service-id}")
    private String tenth2ServiceId;
    @Value("${tenth2.upload-url}")
    private String tenth2UploadUrl;
    @Value("${tenth2.download-url}")
    private String tenth2DownloadUrl;
    @Value("${tenth2.read-key}")
    private String tenth2ReadKey;
    @Value("${tenth2.write-key}")
    private String tenth2WriteKey;

    private static final String XTWG_PUT_KEY = "X-Twg-Put-Option";
    private static final String XTWG_PUT_VALUE = "cplace";
    private static final String CONTENT_TYPE = "text/plain";
    private static final String CONTENT_MD5 = "";
    private static final String IMAGE_TYPE = "image";
    private static final String FILE_TYPE = "file";

    /**
     * 디렉토리 생성
     *
     * @param dirPath
     * @return
     */
    @Override
    public boolean createDirectory(String dirPath) {
        log.debug("tenth2의 파일명은 '디렉토리/파일'의 구조이기 때문에 별도 디렉토리가 존재하지 않습니다.");
        return false;
    }

    /**
     * 파일 저장
     *
     * @param multipartFile
     * @param dirPath
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Override
    public String saveFile(MultipartFile multipartFile, String dirPath) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        return saveFile(multipartFile, dirPath, 0, 0);
    }

    /**
     * 파일 저장
     *
     * @param multipartFile
     * @param dirPath
     * @param width
     * @param height
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Override
    public String saveFile(MultipartFile multipartFile, String dirPath, Integer width, Integer height) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.debug("multipartFile must be not empty.");
            return null;
        }

        String orgFileName = URLEncoder.encode(Objects.requireNonNull(multipartFile.getOriginalFilename()), StandardCharsets.UTF_8);
        String uniqueFileName = StringUtils.getUuidAndRandomAlphanumeric() + "." + StringUtils.getFilenameExtension(orgFileName);

        String contentType = Optional.ofNullable(multipartFile.getContentType()).orElse("").toLowerCase();
        String resize = null;
        if (contentType.startsWith(IMAGE_TYPE) && width > 0 && height > 0) {
            resize = String.format("R%dx%d", width, height);
        }

        String tenth2Url = getTenth2Url(orgFileName, dirPath, uniqueFileName, resize);

        return save(multipartFile, tenth2Url);
    }

    /**
     * 파일 저장 (성공-http status가 200-인 경우에만 json 스트링 형태로 응답한다)
     *
     * @param multipartFile
     * @param tenth2Url
     * @return
     * @throws IOException
     */
    private String save(MultipartFile multipartFile, String tenth2Url) throws IOException {
        URL url = new URL(tenth2Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");

        InputStream inputStream = multipartFile.getInputStream();
        OutputStream outputStream = conn.getOutputStream();

        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        int httpStatusCode = conn.getResponseCode();
        log.debug("HTTP status code : {}", httpStatusCode);

        String responseResult;

        if (httpStatusCode == HttpURLConnection.HTTP_OK) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            responseResult = sb.toString();

            br.close();
        } else {
            responseResult = conn.getResponseMessage();
        }

        conn.disconnect();
        log.debug("tenth2 응답 메시지: {}", responseResult);

        return responseResult;
    }

    /**
     * 파일 URL 조회
     *
     * @param tenth2Path
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    @Override
    public String getFileUrl(String tenth2Path) throws NoSuchAlgorithmException, InvalidKeyException {
        long expires = (System.currentTimeMillis() / 1000) + 60;
        String signature = makeDownloadSignature(tenth2Path, expires);
        String url = "https://" + tenth2DownloadUrl + urlQoute(tenth2Path.getBytes()) + "?download";

        return String.format("%s&TWGServiceId=%s&Expires=%d&Signature=%s", url, tenth2ServiceId, expires, signature);
    }

    /**
     * 파일 삭제(응답값이 200일 경우에만 삭제 성공)
     * ex) tenth2StorageService.deleteFile("/dami/test/ffe8c9cd-ca7e-4131-88ed-a1980d587a9b");
     *
     * @param tenth2Path
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Override
    public int deleteFile(String tenth2Path) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String xtwgPutOrigin = String.format("%s:%s", XTWG_PUT_KEY.toLowerCase(), XTWG_PUT_VALUE);
        String date = getHttpDate();

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE").append("\n")
                .append(CONTENT_MD5).append("\n")
                .append(CONTENT_TYPE).append("\n")
                .append(date).append("\n")
                .append(xtwgPutOrigin).append("\n")
                .append(tenth2Path)
        ;

        String signature = encryptTwg(sb.toString(), tenth2WriteKey);
        String tenth2Url = "http://" + tenth2UploadUrl + tenth2Path;

        URL url = new URL(tenth2Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Date", date);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("Authorization", String.format("TWG %s:%s", tenth2ServiceId, signature));
        conn.setRequestProperty(XTWG_PUT_KEY, XTWG_PUT_VALUE);

        int httpStatusCode = conn.getResponseCode();
        log.debug("HTTP status code : {}, message: {}", httpStatusCode, conn.getResponseMessage());

        conn.disconnect();
        return httpStatusCode;
    }


    /**
     * tenth2 url 구성
     *
     * @param orgFileName
     * @param dirPath
     * @param fileName
     * @param thumbnailSize
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String getTenth2Url(String orgFileName, String dirPath, String fileName, String thumbnailSize) throws NoSuchAlgorithmException, InvalidKeyException {
        String path = dirPath + "/" + fileName;
        long expires = (System.currentTimeMillis() / 1000) + 60;
        String signature = makeUploadSignature(path, expires);
        log.debug("signature: {}", signature);

        String fileType = ObjectUtils.isEmpty(thumbnailSize) ? FILE_TYPE : IMAGE_TYPE;

        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(tenth2UploadUrl).append(path)
                .append("?x-twg-ucc-upload=")
                .append("&type=").append(fileType)
                .append("&filename=").append(orgFileName)
                .append("&Expires=").append(expires)
                .append("&TWGServiceId=").append(tenth2ServiceId)
                .append("&Signature=").append(signature)
        ;

        if (IMAGE_TYPE.equalsIgnoreCase(fileType)) {
            sb.append("&resize=").append(thumbnailSize); // "R300x300"
        }

        log.debug("tenth2Url: {}", sb.toString());
        return sb.toString();
    }

    /**
     * 파일 업로드 서명 구성
     *
     * @param path
     * @param expires
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    private String makeUploadSignature(String path, long expires) throws InvalidKeyException, NoSuchAlgorithmException {
        return urlQoute(encryptTwg(String.format("%s\n%d\n\n\nx-twg-ucc-upload:\n%s", "PUT", expires, path), tenth2WriteKey).getBytes());
    }

    /**
     * 파일 다운로드 서명 구성
     *
     * @param path
     * @param expires
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    private String makeDownloadSignature(String path, long expires) throws InvalidKeyException, NoSuchAlgorithmException {
        return urlQoute(encryptTwg(String.format("%s\n%d\n\n\n\n%s", "GET", expires, path), tenth2ReadKey).getBytes());
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String encryptTwg(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

        // get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

        // get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);

        // compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(data.getBytes());

        byte[] base64buf = Base64.encodeBase64(rawHmac); // requires Apache Commons Codec
        return new String(base64buf);
    }

    /**
     * 날짜 생성
     *
     * @return
     */
    private String getHttpDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US); // Thu, 17 Nov 2005 18:49:58 GMT
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    /**
     * URL 정제
     *
     * @param data
     * @return
     */
    private String urlQoute(byte[] data) {
        final String digits = "0123456789ABCDEF";
        // Guess a bit bigger for encoded form
        StringBuilder buf = new StringBuilder(data.length + 16);
        for (int i = 0; i < data.length; i++) {
            char ch = (char) data[i];
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || "/.-*_".indexOf(ch) > -1) {
                buf.append(ch);
            } else if (ch == ' ') {
                buf.append('+');
            } else {
                buf.append('%');
                buf.append(digits.charAt((data[i] & 0xf0) >> 4));
                buf.append(digits.charAt(data[i] & 0xf));
            }
        }

        return buf.toString();
    }

}
