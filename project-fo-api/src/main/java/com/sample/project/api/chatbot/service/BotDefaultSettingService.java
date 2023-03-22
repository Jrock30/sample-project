package com.sample.project.api.apple.service;

import com.sample.project.api.apple.dto.response.ResponseAppleInfoDto;
import com.sample.project.api.apple.dto.response.RequestUpdAppleInfoDto;
import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.common.exception.CustomException;
import com.sample.project.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_4005;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotDefaultSettingService {
    private final AppleRepository appleRepository;

    /**
     * 운영자 봇 기본설정 페이지
     * 관리자 개별봇 설정 페이지
     * @param botId
     * @return
     */
    public ResponseAppleInfoDto getBotInfo(String botId) {
        AppleEntity apple = appleRepository.findById(botId).orElseThrow(() -> new CustomException(FAIL_4005.message(), HttpStatus.INTERNAL_SERVER_ERROR));

        return ResponseAppleInfoDto.builder()
                .botName(apple.getBotName())
                .welcomeMessage(apple.getWelcomeMessage())
                .talkUseYn(apple.getTalkUseYn())
                .appleSearchId(apple.getAppleSearchId())
                .build();
    }
    /**
     * 운영자 봇 기본설정 수정
     * 관리자 개별봇 설정 수정
     * @param botId
     * @param requestUpdBotInfoDto
     */
    @Transactional
    public void updateBotInfo(String botId, RequestUpdAppleInfoDto requestUpdBotInfoDto) {
        AppleEntity apple = appleRepository.findById(botId).orElseThrow(() -> new CustomException(FAIL_4005.message(), HttpStatus.INTERNAL_SERVER_ERROR));

        apple.changeBotInfo(requestUpdBotInfoDto.getBotName(), requestUpdBotInfoDto.getWelcomeMessage(), requestUpdBotInfoDto.getTalkUseYn(), requestUpdBotInfoDto.getAppleSearchId(), SecurityUtils.getCurrentUserId().get(), LocalDateTime.now());

    }


}
