package com.sample.project.api.apple.service;

import com.sample.project.api.baseguide.dto.ExcelData;
import com.sample.project.api.baseguide.dto.FailExcelDataDto;
import com.sample.project.api.baseguide.dto.response.ResponseUpdateBaseGuideDto;
import com.sample.project.api.apple.dto.request.RequestAdminSearchGuideDto;
import com.sample.project.api.apple.dto.response.ResponseAdminAppleGuideDto;
import com.sample.project.api.apple.dto.response.ResponseAdminGuideDto;
import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.entity.AppleGuideEntity;
import com.sample.project.api.apple.repository.AppleGuideRepository;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.utils.JsonEscapeUtils;
import com.sample.project.common.wrapper.CommonPageSuccessResponse;
import com.sample.project.common.wrapper.Pagination;
import com.sample.project.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAppleGuideSettingService {

    private static final int PAGE_SIZE = 15;

    private final AppleGuideRepository appleGuideRepository;
    private final AppleRepository appleRepository;


    /**
     * admin 봇 도움말 설정 기본 페이지
     *
     * @param botId
     * @param pageNo
     * @return
     */
    public CommonPageSuccessResponse<ResponseAdminAppleGuideDto> getAppleGuideInfo(String botId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE);
        AppleEntity apple = appleRepository.findByBotId(botId);
        Page<ResponseAdminGuideDto> guideList = appleGuideRepository.searchByBotIdAllList(botId, pageable);
        List<ResponseAdminGuideDto> guideCount = appleGuideRepository.searchByBotIdAll(botId);
        List<ResponseAdminGuideDto> useYGuideCount = appleGuideRepository.searchByBotIdUseYAll(botId);

        return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                .appleGuide(guideList.getContent())
                .totalCount(guideCount.size())
                .offCount(guideCount.size() - useYGuideCount.size())
                .onCount(useYGuideCount.size())
                .botName(apple.getBotName())
                .build(), getPaginations(pageable, guideList.getContent()));
    }

    /**
     * 검색 조건에 맞게 검색
     *
     * @param botId
     * @param pageSize
     * @param pageNo
     * @param adminSearchGuideDto
     * @return
     */
    public CommonPageSuccessResponse<ResponseAdminAppleGuideDto> searchAppleList(String botId, int pageSize, int pageNo, RequestAdminSearchGuideDto adminSearchGuideDto) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        AppleEntity apple = appleRepository.findByBotId(botId);
        // 검색어가 없을 때
        if (!StringUtils.hasLength(adminSearchGuideDto.getSearchWord())) {
            if (adminSearchGuideDto.getUseYn() == 0) {
                // 사용조건 0
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotIdUseN(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdN(adminSearchGuideDto, botId).size())
                        .offCount(appleGuideRepository.searchByBotIdN(adminSearchGuideDto, botId).size())
                        .onCount(0)
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdN(adminSearchGuideDto, botId)));
            } else if (adminSearchGuideDto.getUseYn() == 1) {
                // 사용조건 1
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotIdUseY(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdY(adminSearchGuideDto, botId).size())
                        .offCount(0)
                        .onCount(appleGuideRepository.searchByBotIdY(adminSearchGuideDto, botId).size())
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdY(adminSearchGuideDto, botId)));
            } else {
                // 사용조건 전체
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotId(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdALL(adminSearchGuideDto, botId).size())
                        .offCount(appleGuideRepository.searchByBotIdN(adminSearchGuideDto, botId).size())
                        .onCount(appleGuideRepository.searchByBotIdALL(adminSearchGuideDto, botId).size() - appleGuideRepository.searchByBotIdN(adminSearchGuideDto, botId).size())
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdALL(adminSearchGuideDto, botId)));
            }
        } else {
            if (adminSearchGuideDto.getUseYn() == 0) {
                // 사용조건 0
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotIdWithWordUseN(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdWithWordUseN(adminSearchGuideDto, botId, pageable).getContent().size())
                        .offCount(appleGuideRepository.searchByBotIdWithWordUseN(adminSearchGuideDto, botId, pageable).getContent().size())
                        .onCount(0)
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdWithWordUseN(adminSearchGuideDto, botId, pageable).getContent()));
            } else if (adminSearchGuideDto.getUseYn() == 1) {
                // 사용조건 1
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent().size())
                        .offCount(0)
                        .onCount(appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent().size())
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent()));
            } else {
                // 사용조건 전체
                return new CommonPageSuccessResponse<>(ResponseAdminAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchByBotIdWithWordAll(adminSearchGuideDto, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchByBotIdWithWordAll(adminSearchGuideDto, botId, pageable).getContent().size())
                        .offCount(appleGuideRepository.searchByBotIdWithWordAll(adminSearchGuideDto, botId, pageable).getContent().size() - appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent().size())
                        .onCount(appleGuideRepository.searchByBotIdWithWordUseY(adminSearchGuideDto, botId, pageable).getContent().size())
                        .botName(apple.getBotName())
                        .build(), getPaginations(pageable, appleGuideRepository.searchByBotIdWithWordAll(adminSearchGuideDto, botId, pageable).getContent()));
            }
        }
    }

    public Pagination getPaginations(Pageable pageable, List<ResponseAdminGuideDto> appleGuideInfo) {
        Pagination pagination = new Pagination();
        pagination.setPage(pageable.getPageNumber() + 1);
        pagination.setSize(pageable.getPageSize());
        pagination.setTotal((long) appleGuideInfo.size());
        return pagination;
    }

    /**
     * 기존에 저장된 도움말 응답데이터 엑셀로 다운받기
     * 전체 응답 다운로드 버튼
     *
     * @return
     */
    public List<LinkedHashMap<String, Object>> downloadBaseGuideExcel(String botId) {
        List<LinkedHashMap<String, Object>> dataList = new ArrayList<>();
        List<ResponseAdminGuideDto> guideCount = appleGuideRepository.searchByBotIdAll(botId);
        LinkedHashMap<String, Object> baseGuideHeader = new LinkedHashMap<>();

        baseGuideHeader.put("blockCode", "BLOCK CODE");
        baseGuideHeader.put("large", "대분류");
        baseGuideHeader.put("middle", "중분류");
        baseGuideHeader.put("small", "소분류");
        baseGuideHeader.put("useYn", "ON");
        baseGuideHeader.put("baseGuideContent", "기본응답");
        baseGuideHeader.put("guideContent", "수정응답");
        dataList.add(baseGuideHeader);

        for (ResponseAdminGuideDto baseGuides : guideCount) {
            LinkedHashMap<String, Object> baseGuide = new LinkedHashMap<>();

            baseGuide.put("blockCode", baseGuides.getBaseBlockCode());
            baseGuide.put("large", baseGuides.getLargeCategoryName());
            baseGuide.put("middle", baseGuides.getMiddleCategoryName());
            baseGuide.put("small", baseGuides.getSmallCategoryName());
            if (baseGuides.getUseYn() == 1) {
                baseGuide.put("useYn", "y");
            }
            if (baseGuides.getUseYn() == 0) {
                baseGuide.put("useYn", "n");
            }
            baseGuideHeader.put("baseGuideContent", baseGuides.getBaseGuideContent());
            baseGuide.put("guideContent", JsonEscapeUtils.JsonHtmlUnEscape(baseGuides.getGuideContent()));
            dataList.add(baseGuide);
        }
        return dataList;
    }

    /**
     * 엑셀파일에 있는 데이터 받아오기
     *
     * @param file
     * @return
     * @throws IOException
     */
    public List<ExcelData> getExcelData(MultipartFile file) throws CustomException, IOException {
        // 엑셀파일인지 아닌지 확인
        List<ExcelData> dataList = new ArrayList<>();

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new CustomException(FAIL_7018.message(), HttpStatus.BAD_REQUEST);
        }

        Workbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        }

        // 엑셀파일에 있는 데이터 받아오기
        Sheet worksheet = workbook.getSheetAt(0);

        int cells = worksheet.getRow(0).getPhysicalNumberOfCells();
        // 조건 1 : BCDEF열을 삭제안하고 업로드했을 시
        if (cells == 2) {

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                Row row = worksheet.getRow(i);

                ExcelData data = new ExcelData();
                if (row.getCell(0).getCellType() != CellType.STRING) {
                    throw new CustomException(FAIL_7021.message(), HttpStatus.BAD_REQUEST);
                }

                String blockCode = row.getCell(0).getStringCellValue();
                String modContent = row.getCell(1).getStringCellValue();

                if (StringUtils.hasLength(blockCode.trim()) || StringUtils.hasLength(modContent.trim())) {
                    data.setBlockCode(blockCode);
                    data.setModContent(JsonEscapeUtils.JsonHtmlEscape(modContent));
                }

                dataList.add(data);
            }
        } else {
            throw new CustomException(FAIL_7015.message(), HttpStatus.BAD_REQUEST);
        }

        return dataList;
    }

    /**
     * 받아 온 데이터디비에 upd
     *
     * @param dataList
     * @return
     */
    @Transactional
    public ResponseUpdateBaseGuideDto updateBaseGuide(List<ExcelData> dataList, String botId) {
        // 받아 온 데이터 디비에 넣기

        // 실패양식 엑셀에 들어갈 데이터List
        List<FailExcelDataDto> failList = new ArrayList<>();

        // 전체 응답 수정 개수
        int total = dataList.size();
        // 실패개수
        int fail = 0;

        for (ExcelData data : dataList) {
            if (data.getBlockCode() != null) {
                List<AppleGuideEntity> guideList = appleGuideRepository.findAllByBotIdAndBaseBlockCode(botId, data.getBlockCode());
                int size = guideList.size();
                AppleGuideEntity baseGuide = guideList.get(0);

                if (!StringUtils.hasText(data.getModContent())) {
                    // 수정응답이 공란일 경우
                    failList.add(
                            FailExcelDataDto.builder()
                                    .blockCode(baseGuide.getBaseBlockCode())
                                    .largeCatName(baseGuide.getLargeCategoryName())
                                    .middleCatName(baseGuide.getMiddleCategoryName())
                                    .smallCatName(baseGuide.getSmallCategoryName())
                                    .baseResponse("기본응답")
                                    .failReason("정상처리(사용여부만 off)")
                                    .modContent(data.getModContent())
                                    .build()
                    );
                    baseGuide.changeGuideContentUseOff(0, data.getModContent(), SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
                } else {
                    if (size > 0) {
                        if (size == 1) {
                            // 조건 : 응답내용 글자수 초과
                            if (baseGuide.getButtonYn() == 1) {
                                // 400 넘으면 노노
                                if (baseGuide.getGuideContent().length() > 400) {
                                    failList.add(
                                            FailExcelDataDto.builder()
                                                    .blockCode(baseGuide.getBaseBlockCode())
                                                    .largeCatName(baseGuide.getLargeCategoryName())
                                                    .middleCatName(baseGuide.getMiddleCategoryName())
                                                    .smallCatName(baseGuide.getSmallCategoryName())
                                                    .baseResponse("기본응답")
                                                    .failReason("글자수 초과(400자)")
                                                    .modContent(data.getModContent())
                                                    .build()
                                    );
                                    fail++;
                                } else {
                                    baseGuide.changeBaseGuideContent(data.getModContent(), SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
                                }
                            } else {
                                // 1000넘으면 노노
                                if (baseGuide.getGuideContent().length() > 1000) {
                                    failList.add(
                                            FailExcelDataDto.builder()
                                                    .blockCode(baseGuide.getBaseBlockCode())
                                                    .largeCatName(baseGuide.getLargeCategoryName())
                                                    .middleCatName(baseGuide.getMiddleCategoryName())
                                                    .smallCatName(baseGuide.getSmallCategoryName())
                                                    .baseResponse("기본응답")
                                                    .failReason("글자수 초과(1000자)")
                                                    .modContent(data.getModContent())
                                                    .build()
                                    );
                                    fail++;
                                } else {
                                    baseGuide.changeBaseGuideContent(data.getModContent(), SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
                                }
                            }
                        } else if (size > 1) {
                            // 실패한 데이터 엑셀 만들기위한 리스트에 추가
                            // 조건 2 : 동일코드 중복
                            failList.add(
                                    FailExcelDataDto.builder()
                                            .blockCode(baseGuide.getBaseBlockCode())
                                            .largeCatName(baseGuide.getLargeCategoryName())
                                            .middleCatName(baseGuide.getMiddleCategoryName())
                                            .smallCatName(baseGuide.getSmallCategoryName())
                                            .baseResponse("기본응답")
                                            .failReason("동일코드 중복")
                                            .modContent(data.getModContent())
                                            .build()
                            );
                            fail++;

                        }

                    } else {
                        // 조건 3 : 없는 코드
                        // 실패한 데이터 엑셀 만들기위한 리스트에 추가
                        failList.add(
                                FailExcelDataDto.builder()
                                        .blockCode(data.getBlockCode())
                                        .largeCatName("-")
                                        .middleCatName("-")
                                        .smallCatName("-")
                                        .baseResponse("기본응답")
                                        .failReason("없는 코드")
                                        .modContent(data.getModContent())
                                        .build()
                        );
                        fail++;

                    }
                }

            } else {

                throw new CustomException(FAIL_7019.message(), HttpStatus.BAD_REQUEST);
            }
        }


        // 성공개수
        int success = total - fail;
        return ResponseUpdateBaseGuideDto.builder()
                .failList(failList)
                .total(total)
                .success(success)
                .fail(fail)
                .build();
    }
}
