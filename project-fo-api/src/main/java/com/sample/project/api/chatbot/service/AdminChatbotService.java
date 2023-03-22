package com.sample.project.api.apple.service;

import com.sample.project.api.apple.dto.AdminAppleListDto;
import com.sample.project.api.apple.dto.request.*;
import com.sample.project.api.apple.dto.response.ResponseDelegatorBot;
import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.property.DelegateBotProperty;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.service.web.WebClientService;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.security.SecurityUtils;
import com.sample.project.view.ExcelConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

import static com.sample.project.api.apple.enums.DelegationProgress.DP_COMPLETE;
import static com.sample.project.api.apple.enums.DelegationStatus.*;
import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminAppleService {

    private final AppleRepository appleRepository;

    private final DelegateBotProperty delegateBotProperty;

    private final WebClientService webClientService;

    private final ObjectMapper objectMapper;

    /**
     * 관리자 봇 목록 조회
     */
    public Page<AdminAppleListDto> searchBotList(RequestSearchBotListDto requestSearchBotListDto, Pageable pageable) {
        return appleRepository.searchBotList(requestSearchBotListDto, pageable);
    }

    /**
     * 봇 위임-대행중
     */
    @Transactional(rollbackFor = Exception.class)
    public String delegateUnderApple(RequestAppleAdminDelegateDto requestAppleAdminDelegateDto) {

        AppleEntity appleEntity = appleRepository.findByBotId(requestAppleAdminDelegateDto.getBotId());

        if (ObjectUtils.isEmpty(appleEntity)) {
            throw new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST); // 해당 봇 정보를 찾을 수 없습니다.
        }

//        if (appleEntity.getDelegateAgencyStatus().equals(DELEGATION_REQUEST.code())) {
//            throw new CustomException(FAIL_6009.message(), HttpStatus.BAD_REQUEST); // 이미 위임-대행 요청중인 봇 입니다.
//        }

        if (ObjectUtils.isEmpty(requestAppleAdminDelegateDto.getAdminKey())) {
            throw new CustomException(FAIL_4019.message(), HttpStatus.UNAUTHORIZED); // 어드민 키를 찾을 수 없습니다.
        }

        if (ObjectUtils.isEmpty(appleEntity.getAppleSearchId())) {
            throw new CustomException(FAIL_4018.message(), HttpStatus.UNAUTHORIZED); // 카카오 검색 아이디 정보를 찾을 수 없습니다.
        }

        if (ObjectUtils.isEmpty(appleEntity.getMallId())) {
            throw new CustomException(FAIL_4003.message(), HttpStatus.UNAUTHORIZED); // 몰 정보를 찾을 수 없습니다.
        }

        if (ObjectUtils.isEmpty(appleEntity.getAgencyId())) {
            throw new CustomException(FAIL_4017.message(), HttpStatus.UNAUTHORIZED); // Apple24 아이디 정보를 찾을 수 없습니다.
        }

        RequestDelegatorUnderBot requestDelegatorUnderBot = RequestDelegatorUnderBot.builder()
                .delegatorBotId(requestAppleAdminDelegateDto.getBotId())
                .delegationSecretKey(delegateBotProperty.getDelegationSecretKey())
                .delegatorUuid(appleEntity.getAppleSearchId())
                .delegateeBotEmail(delegateBotProperty.getDelegateeBotEmail()) // 대행봇 마스터 이메일
                .delegatorBotEmail(delegateBotProperty.getDelegatorBotEmail()) // 위임봇 마스터 이메일
                .build();

        String resultMessage;

        try {
            ResponseDelegatorBot responseDelegatorBot =
                    webClientService.postAppleDelegateJsonAuth(delegateBotProperty.getDelegateConnectPath(), objectMapper.valueToTree(requestDelegatorUnderBot));

            if (responseDelegatorBot.getStatus().equals("success")) {
                RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
                appleEntity.updateAdminKey(requestAppleAdminDelegateDto.getAdminKey()); // 어드민 키 업데이트
                requestAppleDelegateDto.setDelegationProgress(DP_COMPLETE.code());
                requestAppleDelegateDto.setDelegateAgencyStatus(DELEGATION_UNDER.code());
                appleEntity.delegateApple(requestAppleDelegateDto);
                appleEntity.updateDelegateAgencyFailReason("");
                resultMessage = "SUCCESS";

            } else {
                resultMessage = responseDelegatorBot.getData().getMessage();
                appleEntity.delegateApple(DELEGATION_FAIL_SYSTEM.code());
                appleEntity.updateDelegateAgencyFailReason(resultMessage);
//                throw new CustomException(resultMessage, HttpStatus.BAD_REQUEST);
            }
            return resultMessage;

        } catch (IOException e) {
            log.debug(CommonUtils.getPrintStackTrace(e));
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 봇 위임-대행 실패
     * FAIL_NOT_APP       위임-대행 요청 실패(앱 관리자 미 초대)
     * FAIL_NOT_ADMIN     위임-대행 요청 실패(채널관리자 미 초대)
     * FAIL_SYSTEM        위임-대행 요청 실패(시스템 오류)
     * STOP_NOT_PAY       위임-대행 요청 실패(시스템 오류)
     * STOP_REQUEST       위임-대행 요청 실패(시스템 오류)
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeAppleDelegateStatus(RequestAppleAdminFailDelegateDto requestAppleAdminDelegateDto) {
        List<String> botIdList = requestAppleAdminDelegateDto.getBotIdList();
        String delegateAgencyStatus = requestAppleAdminDelegateDto.getDelegateAgencyStatus();

        if (botIdList.size() > 0) {
            for (String botId : botIdList) {
                AppleEntity appleEntity = appleRepository.findByBotId(botId);

                if (ObjectUtils.isEmpty(appleEntity)) {
                    throw new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST); // 해당 봇 정보를 찾을 수 없습니다.
                }

                if (delegateAgencyStatus.equals(DELEGATION_FAIL_NOT_APP.code())
                        || delegateAgencyStatus.equals(DELEGATION_FAIL_NOT_ADMIN.code())
                        || delegateAgencyStatus.equals(DELEGATION_FAIL_SYSTEM.code())
                        || delegateAgencyStatus.equals(DELEGATION_STOP_NOT_PAY.code())
                        || delegateAgencyStatus.equals(DELEGATION_STOP_REQUEST.code())
                        || delegateAgencyStatus.equals(DELEGATION_DELETE_ADMIN.code())
                ) {
                    appleEntity.delegateApple(delegateAgencyStatus);
                } else {
                    throw new CustomException("botId: " + botId + " \n" + FAIL_4020.message(), HttpStatus.BAD_REQUEST); // 상태가 유효하지 않습니다.
                }
            }
        } else {
            throw new CustomException(FAIL_4021.message(), HttpStatus.BAD_REQUEST); // 봇을 선택해주세요.
        }
    }

    /**
     * 관리자 강제 삭제 (DELETE_ADMIN)
     * <p>
     * 관리자가 해당 봇에 대한 오프라인액션으로서, 카카오채널 및 오픈빌더 에서의 철회, 삭제 등을 처리한 이후 플 래그로서 활용하는 상태값
     */
    @Transactional
    public void adminDeleteApple(String botId) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        AppleEntity appleEntity = appleRepository.findByBotId(botId);

        if (ObjectUtils.isEmpty(appleEntity)) {
            throw new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST); // 해당 봇 정보를 찾을 수 없습니다.
        }
        appleEntity.delegateApple(DELEGATION_DELETE_ADMIN.code());
        appleEntity.deleteApple(userId);
    }

    /**
     * 관리자 봇 상태 리셋(개발 편의를 위한 API)
     */
    public void resetStatusApple(String botId) {

        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        AppleEntity appleEntity = appleRepository.findByBotId(botId);
        appleEntity.changeBotStauts();

        if (ObjectUtils.isEmpty(appleEntity)) {
            throw new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST); // 해당 봇 정보를 찾을 수 없습니다.
        }

    }

    /**
     * 관리자 봇 목록 엑셀데이터 다운로드
     *
     * @param requestSearchBotListDto
     * @return
     */
    public Map<String, Object> downloadAppleListExcel(RequestSearchBotListDto requestSearchBotListDto) {
        // 1. 검색조건에 맞는 챗봇 리스트 가져오기
        List<AdminAppleListDto> appleList = appleRepository.appleList(requestSearchBotListDto);
        // 2. 챗봇 리스트를 엑셀에 데이터 밀어넣기
        return getAppleMap(appleList);
    }

    /**
     * 운영자 챗봇별 도움말 응답 관리
     * 엑셀 다운로드 매핑정보.
     */
    private Map<String, Object> getAppleMap(List<AdminAppleListDto> appleLists) {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "apple_code_excel");
        map.put(ExcelConstant.HEAD, Arrays.asList(
                "no",
                "봇id",
                "쇼핑몰명",
                "봇 이름",
                "봇 상태",
                "봇마스터 ID",
                "카페24 ID",
                "위임봇 ID",
                "봇마스터 연락처",
                "admin key",
                "카카오채널 검색용 ID",
                "봇 생성일",
                "위임-대행 요청일",
                "위임-대행 연결(성공)일",
                "위임-대행 연결(실패)일",
                "위임-대행 중지 일자"));

        List<List<String>> appleList = new ArrayList<>();
        List<String> data = new ArrayList<>();
        for (int i = 0; i < appleLists.size(); i++) {
            data = new ArrayList<>();
            AdminAppleListDto code = appleLists.get(i);
            // appleList에 값을 넣어서 엑셀의 row 한개의 컬럼값을 채워준다
            data.add(String.valueOf(i+1)); // no
            data.add(code.getDelegatorBotId()); // 봇아이디
            data.add(code.getMallId()); // 쇼핑몰명
            data.add(code.getBotName()); // 봇 이름
            data.add(code.getDelegateAgencyStatusName()); // 봇 상태
            data.add(code.getBotMasterUserId()); // 봇마스터 ID
            data.add(code.getAgencyId()); // 카페24 ID
            data.add(code.getDelegatorBotId()); // 위임봇 ID
            data.add(code.getMobile()); // 봇마스터 연락처
            data.add(code.getAdminKey()); // admin key
            data.add(code.getAppleSearchId()); // 카카오채널 검색용 ID
            data.add(String.valueOf(code.getRegDate())); // 봇 생성일
            data.add(String.valueOf(code.getDelegateRequestDate())); // 위임-대행 요청일
            data.add(String.valueOf(code.getDelegateUnderDate())); // 위임-대행 연결(성공)일
            data.add(String.valueOf(code.getDelegateFailDate())); // 위임-대행 연결(실패)일
            data.add(String.valueOf(code.getDelegateStopDate())); // 위임-대행 중지 일자

            appleList.add(data);
        }

        map.put(ExcelConstant.BODY, appleList);
        return map;
    }
}
