package com.sample.project.common.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * 디렉토리 생성
     *
     * @param dirPath
     * @return
     * @throws Exception
     */
    boolean createDirectory(String dirPath) throws Exception;

    /**
     * 파일 저장
     *
     * @param multipartFile
     * @param dirPath
     * @return
     */
    String saveFile(MultipartFile multipartFile, String dirPath) throws Exception;

    /**
     * 이미지 파일 저장, 이미지 파일이면서 width x height 가 0보다 클 경우 썸네일 생성
     *
     * @param multipartFile
     * @param dirPath
     * @param width
     * @param height
     * @return
     */
    String saveFile(MultipartFile multipartFile, String dirPath, Integer width, Integer height) throws Exception;

    /**
     * 파일 URL 조회
     *
     * @param tenth2Path
     * @return
     * @throws Exception
     */
    String getFileUrl(String tenth2Path) throws Exception;

    /**
     * 파일 삭제
     *
     * @param tenth2Path
     * @return
     * @throws Exception
     */
    int deleteFile(String tenth2Path) throws Exception;
}
