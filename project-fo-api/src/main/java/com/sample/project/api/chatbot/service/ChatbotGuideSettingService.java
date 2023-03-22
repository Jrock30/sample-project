package com.sample.project.api.apple.service;

import com.sample.project.api.baseguide.entity.AppleBaseGuideEntity;
import com.sample.project.api.baseguide.repository.AdminBaseGuideRepository;
import com.sample.project.api.apple.dto.request.RequestChangedGuideContentDto;
import com.sample.project.api.apple.dto.request.RequestCheckedAppleGuideDto;
import com.sample.project.api.apple.dto.request.RequestCheckedAppleToBaseGuideDto;
import com.sample.project.api.apple.dto.request.RequestSearchGuideDto;
import com.sample.project.api.apple.dto.response.ResponseBackBaseGuideDto;
import com.sample.project.api.apple.dto.response.ResponseAppleGuideDto;
import com.sample.project.api.apple.dto.response.ResponseGuideDto;
import com.sample.project.api.apple.dto.response.ResponseUpdBaseGuidePopUpDto;
import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.entity.AppleGuideEntity;
import com.sample.project.api.apple.repository.*;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.utils.JsonEscapeUtils;
import com.sample.project.common.wrapper.CommonPageSuccessResponse;
import com.sample.project.common.wrapper.Pagination;
import com.sample.project.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleGuideSettingService {

    private static final int PAGE_SIZE = 15;

    private final AppleGuideRepository appleGuideRepository;
    private final AdminBaseGuideRepository appleBaseGuideRepository;
    private final AppleRepository appleRepository;
    private final AppleLargeCategoryRepository largeCategoryRepository;
    private final AppleMiddleCategoryRepository middleCategoryRepository;
    private final AppleSmallCategoryRepository smallCategoryRepository;

    /**
     * user 봇 도움말 설정 기본 페이지
     *
     * @param botId
     * @param pageNo
     * @return
     */
    public CommonPageSuccessResponse<ResponseAppleGuideDto> getAppleGuideInfo(String botId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE);
        Page<ResponseGuideDto> guideList = appleGuideRepository.searchAppleByBotIdAllList(botId, pageable);
        List<ResponseGuideDto> guideCount = appleGuideRepository.searchAppleByBotIdAll(botId);
        List<ResponseGuideDto> useYGuideCount = appleGuideRepository.searchAppleByBotIdUseYAll(botId);

        return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                .appleGuide(guideList.getContent())
                .totalCount(guideCount.size())
                .offCount(guideCount.size() - useYGuideCount.size())
                .onCount(useYGuideCount.size())
                .build(), getPagination(pageable, guideCount));
    }


    /**
     * 도움말별 사용여부 토글 수정
     *
     * @param botId
     * @param baseBlockCode
     * @param useYn
     */
    @Transactional
    public void updUseYnAppleBaseGuide(String botId, String baseBlockCode, int useYn) {
        AppleGuideEntity appleGuide = appleGuideRepository.findByBotIdAndBaseBlockCode(botId, baseBlockCode).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));
        if (useYn == 1) {
            appleGuide.changeUseYn(0, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
        }
        if (useYn == 0) {
            appleGuide.changeUseYn(1, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
        }
    }

    /**
     * 도움말별 수정버튼 팝업
     *
     * @param botId
     * @param baseBlockCode
     * @return
     */
    public ResponseUpdBaseGuidePopUpDto getUseYnAppleBaseGuidePopUp(String botId, String baseBlockCode) {
        AppleGuideEntity appleGuide = appleGuideRepository.findByBotIdAndBaseBlockCode(botId, baseBlockCode).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));
        return ResponseUpdBaseGuidePopUpDto.builder()
                .guideContent(appleGuide.getGuideContent())
                .largeCategoryName(appleGuide.getLargeCategoryName())
                .middleCategoryName(appleGuide.getMiddleCategoryName())
                .smallCategoryName(appleGuide.getSmallCategoryName())
                .guideContentLengh(appleGuide.getGuideContent().length())
                .buttonYn(appleGuide.getButtonYn())
                .build();
    }

    /**
     * 기본응답 버튼
     *
     * @param baseBlockCode
     * @return
     */
    public ResponseBackBaseGuideDto getBackBaseGuideContent(String baseBlockCode) {
        return ResponseBackBaseGuideDto.builder()
                .guideContent(
                        appleBaseGuideRepository.findById(baseBlockCode)
                                .orElseThrow(
                                        () -> new CustomException(FAIL_7011.message(), HttpStatus.BAD_REQUEST)
                                ).getGuideContent()
                )
                .build();
    }


    /**
     * 도움말 개별 응답내용 수정데이터 저장
     *
     * @param botId
     * @param changedGuideContent
     */
    @Transactional
    public void updAppleGuide(String botId, RequestChangedGuideContentDto changedGuideContent) {
        AppleGuideEntity appleGuide = appleGuideRepository.findByBotIdAndBaseBlockCode(botId, changedGuideContent.getBaseBlockCode()).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));
        int buttonYn = appleGuide.getButtonYn();
        String content = JsonEscapeUtils.JsonHtmlEscape(changedGuideContent.getGuideContent());
        if (!StringUtils.hasText(content)) {
            appleGuide.changeGuideContentUseOff(0, content, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
        } else {
            if (buttonYn == 1 && content.length() < 400) {
                appleGuide.changeGuideContent(content, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
            } else if (buttonYn == 0 && content.length() < 1000) {
                appleGuide.changeGuideContent(content, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
            } else {
                throw new CustomException(FAIL_7016.message(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }


    /**
     * 선택한 블록 기본응답으로 수정
     *
     * @param botId
     * @param requestCheckedAppleToBaseGuideDto
     */
    @Transactional
    public void updCheckedGuidesToBaseGuide(String botId, RequestCheckedAppleToBaseGuideDto requestCheckedAppleToBaseGuideDto) {
        for (String baseBlockCode : requestCheckedAppleToBaseGuideDto.getCheckedBlockCode()) {
            AppleGuideEntity appleGuide = appleGuideRepository.findByBotIdAndBaseBlockCode(botId, baseBlockCode).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));
            AppleBaseGuideEntity baseGuide = appleBaseGuideRepository.findById(baseBlockCode).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));
            appleGuide.changeGuideContent(baseGuide.getGuideContent(), SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
        }
    }


    /**
     * 선택한 블록 사용여부 ON/OFF으로 수정
     *
     * @param botId
     * @param requestCheckedAppleGuideDto
     */
    @Transactional
    public void updCheckedGuidesToUseOnOff(String botId, RequestCheckedAppleGuideDto requestCheckedAppleGuideDto) {
        for (String checkedBlockInfo : requestCheckedAppleGuideDto.getCheckedBlockCode()) {
            int useYn = requestCheckedAppleGuideDto.getUseYn();

            AppleGuideEntity appleGuide = appleGuideRepository.findByBotIdAndBaseBlockCode(botId, checkedBlockInfo).orElseThrow(() -> new CustomException(FAIL_7014.message(), HttpStatus.BAD_REQUEST));

            if (useYn == 1) {
                appleGuide.changeUseYn(1, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
            } else {
                appleGuide.changeUseYn(0, SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());
            }
        }
    }


    /**
     * 검색 조건에 맞게 검색
     *
     * @param botId
     * @param pageSize
     * @param pageNo
     * @param searchCondition
     * @return
     */
    @Transactional
    public CommonPageSuccessResponse<ResponseAppleGuideDto> searchGuideList(String botId, int pageSize, int pageNo, RequestSearchGuideDto searchCondition) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        int useYn = searchCondition.getUseYn();
        // 검색어가 없을 때
        if (!StringUtils.hasLength(searchCondition.getSearchWord())) {
            if (useYn == 0) {
                // 사용조건 0
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotIdUseN(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdN(searchCondition, botId).size())
                        .offCount(appleGuideRepository.searchAppleByBotIdN(searchCondition, botId).size())
                        .onCount(0)
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdN(searchCondition, botId)));
            } else if (useYn == 1) {
                // 사용조건 1
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotIdUseY(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdY(searchCondition, botId).size())
                        .offCount(0)
                        .onCount(appleGuideRepository.searchAppleByBotIdY(searchCondition, botId).size())
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdY(searchCondition, botId)));
            } else {
                // 사용조건 전체
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotId(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdALL(searchCondition, botId).size())
                        .offCount(appleGuideRepository.searchAppleByBotIdN(searchCondition, botId).size())
                        .onCount(appleGuideRepository.searchAppleByBotIdALL(searchCondition, botId).size() - appleGuideRepository.searchAppleByBotIdN(searchCondition, botId).size())
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdALL(searchCondition, botId)));
            }
        } else {
            // 검색어가 있을 때
            if (useYn == 0) {
                // 사용조건 0
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotIdWithWordUseN(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdWithWordUseN(searchCondition, botId, pageable).getContent().size())
                        .offCount(appleGuideRepository.searchAppleByBotIdWithWordUseN(searchCondition, botId, pageable).getContent().size())
                        .onCount(0)
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdWithWordUseN(searchCondition, botId, pageable).getContent()));
            } else if (useYn == 1) {
                // 사용조건 1
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent().size())
                        .offCount(0)
                        .onCount(appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent().size())
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent()));
            } else {
                // 사용조건 전체
                return new CommonPageSuccessResponse<>(ResponseAppleGuideDto.builder()
                        .appleGuide(appleGuideRepository.searchAppleByBotIdWithWordAll(searchCondition, botId, pageable).getContent())
                        .totalCount(appleGuideRepository.searchAppleByBotIdWithWordAll(searchCondition, botId, pageable).getContent().size())
                        .offCount(appleGuideRepository.searchAppleByBotIdWithWordAll(searchCondition, botId, pageable).getContent().size() - appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent().size())
                        .onCount(appleGuideRepository.searchAppleByBotIdWithWordUseY(searchCondition, botId, pageable).getContent().size())
                        .build(), getPagination(pageable, appleGuideRepository.searchAppleByBotIdWithWordAll(searchCondition, botId, pageable).getContent()));
            }
        }
    }

    public Pagination getPagination(Pageable pageable, List<ResponseGuideDto> appleGuideInfo) {
        Pagination pagination = new Pagination();
        pagination.setPage(pageable.getPageNumber() + 1);
        pagination.setSize(pageable.getPageSize());
        pagination.setTotal((long) appleGuideInfo.size());
        return pagination;
    }


    /**
     * 봇아이디 유효성 검사
     *
     * @param botId
     */
    public Optional<AppleEntity> checkValidationBotId(String botId) {
        return appleRepository.findById(botId);
    }
}


