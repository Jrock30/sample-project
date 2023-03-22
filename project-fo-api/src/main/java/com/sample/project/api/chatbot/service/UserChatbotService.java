package com.sample.project.api.apple.service;

import com.sample.project.api.access.service.AccessService;
import com.sample.project.api.auth.service.AuthService;
import com.sample.project.api.apple.dto.request.RequestAppleDelegateDto;
import com.sample.project.api.apple.dto.request.RequestDelegatorBot;
import com.sample.project.api.apple.dto.request.RequestUseInfoDto;
import com.sample.project.api.apple.dto.response.ResponseBotUserListDto;
import com.sample.project.api.apple.dto.response.ResponseDelegatorBot;
import com.sample.project.api.apple.entity.AppleBaseGuideEntity;
import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.entity.AppleGuideEntity;
import com.sample.project.api.apple.entity.ApplePermissionEntity;
import com.sample.project.api.apple.property.DelegateBotProperty;
import com.sample.project.api.apple.repository.AppleBaseGuideRepository;
import com.sample.project.api.apple.repository.AppleGuideRepository;
import com.sample.project.api.apple.repository.ApplePermissionRepository;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.request.RequestMarketingMessageDto;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.service.web.WebClientService;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.utils.StringUtils;
import com.sample.project.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.sample.project.api.apple.enums.BotRole.BOT_MASTER;
import static com.sample.project.api.apple.enums.DailyReportStatus.DR_RECEIVING;
import static com.sample.project.api.apple.enums.DelegationProgress.DP_FIRST;
import static com.sample.project.api.apple.enums.DelegationProgress.DP_SECOND;
import static com.sample.project.api.apple.enums.DelegationStatus.*;
import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserAppleService {

    private final AppleRepository appleRepository;

    private final ApplePermissionRepository applePermissionRepository;

    private final AppleBaseGuideRepository appleBaseGuideRepository;

    private final AppleGuideRepository appleGuideRepository;

    private final WebClientService webClientService;

    private final AccessService accessService;

    private final Apple24MarketingMessageSub apple24MarketingMessageSub;

    private final ObjectMapper objectMapper;

    private final DelegateBotProperty delegateBotProperty;

    private final AuthService authService;

    /**
     * 위임봇 생성
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBot(String hmac) {

        if (StringUtils.isNotEmpty(hmac)) { // hmac 이 있을 때 hmac 의 정보를 매칭해서 apple24 정보를 회원에 넣어준다.
            if (!accessService.hmacValidAndApple24InfoRegit(hmac)) {
                throw new CustomException(FAIL_4024.message(), HttpStatus.FORBIDDEN); // 카페24를 통한 접근이 2시간이 지났습니다. 카페24를 통해 다시 접근해 주세요.
            }
        }
        accessService.validateCreateBotHmac(); // hmac 이 없으면 히스토리에 있는 정보를 가지고 유효성 체크 (apple24 정보 및 봇 생성 여부 체크)

        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        String mallId = SecurityUtils.getMallId().orElseThrow(
                () -> new CustomException(FAIL_4003.message(), HttpStatus.UNAUTHORIZED)); // 몰 정보를 찾을 수 없습니다.

        String mallName = SecurityUtils.getMallName().orElse(null);

        String agencyId = SecurityUtils.getAgencyId().orElseThrow(
                () -> new CustomException(FAIL_4017.message(), HttpStatus.UNAUTHORIZED)); // Apple24 아이디 정보를 찾을 수 없습니다.

        String botId;

        try {
            RequestDelegatorBot requestDelegatorBot = RequestDelegatorBot.builder()
                    .delegationSecretKey(delegateBotProperty.getDelegationSecretKey())
                    .delegatorBotEmail(delegateBotProperty.getDelegatorBotEmail())
                    .delegatorBotName(mallId)
                    .build();

            // 위임봇 생성
            ResponseDelegatorBot responseDelegatorBot =
                    webClientService.postAppleDelegateJsonAuth(delegateBotProperty.getDelegateCreatePath(), objectMapper.valueToTree(requestDelegatorBot));

            if (responseDelegatorBot.getStatus().equals("success")) {
                botId = responseDelegatorBot.getData().getCreatedDelegatorBotId();
            } else {
                String resultMessage = responseDelegatorBot.getData().getMessage();
                throw new CustomException(resultMessage, HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            log.debug(CommonUtils.getPrintStackTrace(e));
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * TODO
         *  - 차후 챗봇 관련 엔티티 재 설계
         */
        // 챗봇 등록
        AppleEntity appleEntity = AppleEntity.builder()
                .botId(botId)
                .botName(mallName)
                .mallId(mallId)
                .mallName(mallName)
                .appleSearchId(null)
                .agencyId(agencyId)
                .delegateAgencyStatus(DELEGATION_BEFORE.code())
                .delegationProgress(DP_FIRST.code())
                .talkUseYn(0)
                .welcomeMessage("")
                .delegateAgencyFailReason(null)
                .deleteYn(0)
                .regId(userId)
                .updId(userId)
                .build();
        appleRepository.save(appleEntity);

        // 챗봇 권한 등록
        ApplePermissionEntity applePermissionEntity = ApplePermissionEntity.builder()
                .botId(botId)
                .permissionGroupId(BOT_MASTER.code())
                .permissionGroupName(BOT_MASTER.name())
                .userId(userId)
                .reportAcceptStatus(DR_RECEIVING.code())
                .withdrawalYn(0)
                .regId(userId)
                .updId(userId)
                .build();
        applePermissionRepository.save(applePermissionEntity);

        // 챗봇 도움말 기본 도움말 등록 TODO - 벌크업 수정
        List<AppleBaseGuideEntity> baseGuideList = appleBaseGuideRepository.findAll();
        baseGuideList.forEach(baseGuide -> {
            AppleGuideEntity appleGuideEntity = AppleGuideEntity.builder()
                    .baseBlockCode(baseGuide.getBaseBlockCode())
                    .botId(botId)
                    .builderBlockId(baseGuide.getBuilderBlockId())
                    .guideContent(baseGuide.getGuideContent())
                    .useYn(baseGuide.getUseYn())
                    .regId(userId)
                    .regDate(LocalDateTime.now())
                    .updId(userId)
                    .updDate(LocalDateTime.now())
                    .smallCategoryCode(baseGuide.getSmallCategoryCode())
                    .middleCategoryCode(baseGuide.getMiddleCategoryCode())
                    .largeCategoryCode(baseGuide.getLargeCategoryCode())
                    .smallCategoryName(baseGuide.getSmallCategoryName())
                    .middleCategoryName(baseGuide.getMiddleCategoryName())
                    .largeCategoryName(baseGuide.getLargeCategoryName())
                    .build();
            appleGuideRepository.save(appleGuideEntity);
        });

        // 마케팅 메시지 초기 생성
        RequestMarketingMessageDto requestMarketingMessageDto = new RequestMarketingMessageDto();
        requestMarketingMessageDto.setBotId(botId);
        apple24MarketingMessageSub.campaignMessageInitSettingSave(requestMarketingMessageDto);
    }

    /**
     * 사용자 챗봇 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseBotUserListDto> searchUserBotList() {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED));
        return appleRepository.searchUserBotList(userId);
    }

    /**
     * 사용자 챗봇 위임-대행 요청
     */
    @Transactional
    public void delegateApple(RequestAppleDelegateDto requestAppleDelegateDto) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        requestAppleDelegateDto.setUserId(userId);

        ResponseBotUserListDto responseBotUserListDto = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

        if (!responseBotUserListDto.getPermissionGroupId().equals(BOT_MASTER.code())) { // 봇의 마스터가 아니면 요청 불가
            throw new CustomException(FAIL_6005.message(), HttpStatus.BAD_REQUEST); // 마스터만 요청할 수 있습니다.
        }

//        if (!responseBotUserListDto.getDelegationProgress().equals(DP_COMPLETE.code())) { // 챗봇 진행 중인 상태가 완료가 아니면
//            throw new CustomException(FAIL_6008.message(), HttpStatus.BAD_REQUEST); // 챗봇 요청 진행이 완료되지 않았습니다.
//        }

        String delegateAgencyStatus = responseBotUserListDto.getDelegateAgencyStatus();

        if (delegateAgencyStatus.equals(DELEGATION_REQUEST.code())) { // 위임-대행 대행 중일 때 실패
            throw new CustomException(FAIL_6009.message(), HttpStatus.BAD_REQUEST); // 이미 위임-대행 요청중인 봇 입니다.
        }

        if (delegateAgencyStatus.equals(DELEGATION_UNDER.code())) { // 위임-대행 대행 중일 때 실패
            throw new CustomException(FAIL_6003.message(), HttpStatus.BAD_REQUEST); // 이미 대행중인 봇 입니다.
        }

        if (SecurityUtils.getMobile().isEmpty()) { // 핸드폰 번호가 인증이 안되어 있을 시 인증 (인증번호 체크할 때 번호가 등록 되어 있다)
//        if (StringUtils.isNotEmpty(requestAppleDelegateDto.getMobile())) { // 핸드폰 번호가 올라오면 인증
            throw new CustomException(FAIL_4026.message(), HttpStatus.BAD_REQUEST); // 모바일 인증을 해주세요.
//            authService.checkNotTimeAuthNumber(requestAppleDelegateDto.getMobile(), requestAppleDelegateDto.getAuthNumber());
        }

        AppleEntity appleEntity = appleRepository.getById(responseBotUserListDto.getBotId());
        requestAppleDelegateDto.setDelegateAgencyStatus(DELEGATION_REQUEST.code());
        requestAppleDelegateDto.setDelegationProgress(DP_SECOND.code());
        appleEntity.delegateApple(requestAppleDelegateDto);
        appleEntity.updateAppleSearchId("@"+requestAppleDelegateDto.getAppleSearchId());
    }

    /**
     * 사용자 위임-대행 철회
     */
    @Transactional
    public void delegateWithdrawApple(String botId) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
        requestAppleDelegateDto.setUserId(userId);
        requestAppleDelegateDto.setBotId(botId);

        ResponseBotUserListDto apple = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

        if (!apple.getPermissionGroupId().equals(BOT_MASTER.code())) { // 봇의 마스터가 아니면 요청 불가
            throw new CustomException(FAIL_6005.message(), HttpStatus.BAD_REQUEST); // 마스터만 요청할 수 있습니다.
        }

        String delegateAgencyStatus = apple.getDelegateAgencyStatus();

        if (!delegateAgencyStatus.equals(DELEGATION_REQUEST.code())
                && !delegateAgencyStatus.equals(DELEGATION_UNDER.code())) { // 위임-대행 요청중 그리고 대행중이 아닐 때 실패
            throw new CustomException(FAIL_6004.message(), HttpStatus.BAD_REQUEST); // 철회할 수 없는 상태 입니다.
        }

        AppleEntity appleEntity = appleRepository.getById(apple.getBotId());
        requestAppleDelegateDto.setDelegateAgencyStatus(DELEGATION_STOP_REQUEST.code()); // 위임-대행 중지(요청)
        requestAppleDelegateDto.setDelegationProgress(DP_FIRST.code());
        appleEntity.delegateApple(requestAppleDelegateDto);
    }


    /**
     * 사용자 데일리 리포트 수신 요청, 취소 (운영자)
     */
    @Transactional
    public void requestOrCancelDailyReport(String botId, String reportAcceptStatus) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
        requestAppleDelegateDto.setUserId(userId);
        requestAppleDelegateDto.setBotId(botId);

        // 위임상태에 따른 분기를 줄 경우 사용 그리고 봇에 회원 있는지 여부도 체크는 조회하면서 자동으로 체크
        ResponseBotUserListDto apple = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

        ApplePermissionEntity applePermissionEntity = appleRepository.searchUserApplePermission(requestAppleDelegateDto);
        applePermissionEntity.changeReportAcceptStatus(reportAcceptStatus, userId);
    }

    /**
     * 사용자 데일리 리포트 수신 요청 수락, 거절 (마스터)
     */
    @Transactional
    public void acceptOrRefuseDailyReport(String botId, RequestUseInfoDto requestUseInfoDto, String reportAcceptStatus) {
        String userId = requestUseInfoDto.getUserId(); // 수신 요청을 한 운영자

        RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
        requestAppleDelegateDto.setUserId(userId);
        requestAppleDelegateDto.setBotId(botId);

        // 위임상태에 따른 분기를 줄 경우 사용 그리고 봇에 회원 있는지 여부도 체크는 조회하면서 자동으로 체크
        ResponseBotUserListDto apple = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

//        if (!apple.getPermissionGroupId().equals(BOT_MASTER.code())) { // 봇의 마스터가 아니면 요청 불가
//            throw new CustomException(FAIL_6005.message(), HttpStatus.BAD_REQUEST); // 마스터만 요청할 수 있습니다.
//        }
        if (!applePermissionRepository.findByUserIdAndBotId(
                SecurityUtils.getCurrentUserId().orElseThrow(() -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)),
                botId).orElseThrow(() -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)).getPermissionGroupId().equals(BOT_MASTER.code())
        ) {
            throw new CustomException(FAIL_6006.message(), HttpStatus.BAD_REQUEST);
        }



        ApplePermissionEntity applePermissionEntity = appleRepository.searchUserApplePermission(requestAppleDelegateDto);
        applePermissionEntity.changeReportAcceptStatus(reportAcceptStatus, userId);
    }

    /**
     * 사용자(마스터, 운영자) 봇 탈퇴 요청
     */
    @Transactional(rollbackFor = Exception.class)
    public void userBotWithdrawal(String botId) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(
                () -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED)); // 회원정보를 찾을 수 없습니다.

        RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
        requestAppleDelegateDto.setUserId(userId);
        requestAppleDelegateDto.setBotId(botId);

        ResponseBotUserListDto apple = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

        if (apple.getDelegateAgencyStatus().equals(DELEGATION_UNDER.code())) { // 위임-대행중인 경우 삭제 불가
            throw new CustomException(FAIL_6002.message(), HttpStatus.BAD_REQUEST);
        }

        if (apple.getPermissionGroupId().equals(BOT_MASTER.code())) { // 봇의 마스터면 봇 삭제 및 모든 운영자 탈퇴 처리
            AppleEntity appleEntity = appleRepository.getById(botId);
            List<ApplePermissionEntity> applePermissionEntityList = applePermissionRepository.findByBotId(botId).orElseThrow(
                    () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.

            appleEntity.deleteApple(userId);
            applePermissionEntityList.forEach(applePermissionEntity -> applePermissionEntity.appleWithdrawal(userId));

        } else { // 봇의 운영자면 해당 권한만 탈퇴처리
            ApplePermissionEntity applePermissionEntity = applePermissionRepository.findByUserIdAndBotId(userId, botId).orElseThrow(
                    () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.
            applePermissionEntity.appleWithdrawal(userId);
        }
    }

    /**
     * 봇에 대한 회원의 권한 조회
     */
    public String getBotUserPermission(String botId, String userId) {
        RequestAppleDelegateDto requestAppleDelegateDto = new RequestAppleDelegateDto();
        requestAppleDelegateDto.setUserId(userId);
        requestAppleDelegateDto.setBotId(botId);

        ResponseBotUserListDto apple = appleRepository.searchUserBotInfo(requestAppleDelegateDto).orElseThrow(
                () -> new CustomException(FAIL_4005.message(), HttpStatus.BAD_REQUEST)); // 해당 봇 정보를 찾을 수 없습니다.
        return apple.getPermissionGroupId();
    }
}
