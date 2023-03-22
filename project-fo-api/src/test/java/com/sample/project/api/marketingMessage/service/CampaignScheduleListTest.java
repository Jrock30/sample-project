package com.sample.project.api.marketingMessage.service;


import com.sample.project.projectFoApiApplication;
import com.sample.project.api.login.dto.RequestLoginDto;
import com.sample.project.api.login.service.LoginService;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.CampaignDto;
import com.sample.project.api.marketingMessage.dto.request.RequestCampaignScheduleDto;
import com.sample.project.api.marketingMessage.util.MarketingUtil;
import com.sample.project.common.exception.DefaultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = projectFoApiApplication.class)
public class CampaignScheduleListTest {

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;
    @Autowired
    private MarketingUtil marketingUtil;

    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("캠페인 일정 목록 조회 테스트")
    public void campaignListSearchTest() throws DefaultException {
        //given
        String baseMonth = "2022-11";
        LocalDate compareStartLocalDate = marketingUtil.convertLocaldate(baseMonth, "start");
        RequestCampaignScheduleDto requestCampaignScheduleDto = new RequestCampaignScheduleDto();
        requestCampaignScheduleDto.setBotId("damiadmin12#$");

        //when
        List<CampaignDto>campaignDtoList = apple24MarketingMessageSub.getCampaignScheduleList(requestCampaignScheduleDto);

        //then
        assertAll(
                ()-> assertEquals(3, campaignDtoList.size()),
                ()-> assertTrue(campaignDtoList.stream()
                        .filter(item -> !Objects.isNull(item.getSendEndDate()))
                        .anyMatch(item -> (item.getSendStartDate().isAfter(compareStartLocalDate))))
        );
    }

    @BeforeEach
    public void before() {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setUserId("admin@damiadmin.com");
        requestLoginDto.setPassword("damiadmin12#$");

        ResponseEntity<?> login = loginService.login(requestLoginDto);

        assertAll(
                () -> assertNotNull(login.getBody())
        );
    }
}
