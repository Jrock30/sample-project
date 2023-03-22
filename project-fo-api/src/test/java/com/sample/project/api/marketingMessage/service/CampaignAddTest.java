package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.login.dto.RequestLoginDto;
import com.sample.project.api.login.service.LoginService;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.BackGroundColor;
import com.sample.project.api.marketingMessage.dto.CampaignDto;
import com.sample.project.api.marketingMessage.dto.reponse.MarketingResultDetailDto;
import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.api.marketingMessage.enums.CampaignContent;
import com.sample.project.api.marketingMessage.enums.CampaignSendCycle;
import com.sample.project.api.marketingMessage.enums.CampaignTargetType;
import com.sample.project.api.marketingMessage.repository.CampaignRepository;
import com.sample.project.api.marketingMessage.util.MarketingUtil;
import com.sample.project.common.utils.CommonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = projectFoApiApplication.class)
public class CampaignAddTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MarketingMessageService marketingMessageService;

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MarketingUtil marketingUtil;

    @Test
    @DisplayName("캠페인 타입 1 추가 테스트")
    void campaignType01FullAddTest(){
        //case 1 타켓 설정이 테스트 번호 입력 일 경우
        //       상품 설정이 신상품
        //       발송 주기 한번만
        //       카드 타입 리스트 형

        // given
        CampaignDto campaignDto =
                CampaignDto.builder()
                        .campaignNo(0L)
                        .botId("123")
                        .cardTypeCode("CARD_01")
                        .color("RED")
                        .campaignName("캠페인 테스트")
                        .targetTypeCode(CampaignTargetType.TARGET_02.getCode())
                        .targetTestMobile("01054652777")
                        .contentTypeCode(CampaignContent.CONTENT_01.getCode())
                        .contentBaseMonth("8")
                        .sendTypeCode(CampaignSendCycle.SEND_CY_01.getCode())
                        .sendStartDate(LocalDate.now())
                        .sendDateTime("11:38")
                        .build();

        // when
        marketingMessageService.campaignInfoSave(campaignDto);
        List<CampaignEntity> campaignEntities = campaignRepository.findAll();

        //then
        assertAll(
                ()-> assertTrue(campaignEntities.size()>0),
                ()-> assertTrue(campaignEntities.stream()
                        .filter(item -> !item.getBotId().isBlank())
                        .filter(item -> !item.getCardTypeCode().isBlank())
                        .filter(item -> !item.getCampaignName().isBlank())
                        .filter(item -> !item.getTargetTypeCode().isBlank())
                        .filter(item -> !item.getTargetTestMobile().isBlank())
                        .filter(item -> !item.getContentTypeCode().isBlank())
                        .filter(item -> !item.getContentBaseMonth().isBlank())
                        .filter(item -> !item.getSendTypeCode().isBlank())
                        .filter(item -> !item.getCardTypeCode().isBlank())
                        .anyMatch(item -> StringUtils.hasLength(item.getSendDateTime())))
        );
    }

    //case 2 타켓 설정이 최근 구매 경험 고객
    //       상품 설정이 최근 n개월 이내 구매 경험 고객
    //       발송 주기 반복 발송
    //       커머스 타입 리스트 형

    @Test
    @DisplayName("캠페인 타입 2 추가 테스트")
    void campaignType02FullAddTest(){
        // given
        List<String> requestSendIterationDayWeekList = new ArrayList<>();
        requestSendIterationDayWeekList.add("MON");
        requestSendIterationDayWeekList.add("TUE");

        BackGroundColor backGroundColor = BackGroundColor.builder()
                .colorCode("#FFF8CC")
                .colorName("노란색")
                .build();

        CampaignDto campaignDto =
                CampaignDto.builder()
                        .campaignNo(0L)
                        .botId("637c56ce8f7dc436c344fcbparker")
                        .cardTypeCode("CARD_02")
                        .backGroundColor(backGroundColor)
                        .campaignName("캠페인 테스트_02")
                        .targetTypeCode(CampaignTargetType.TARGET_02.getCode())
                        .targetBaseMonth("3")
                        .contentTypeCode(CampaignContent.CONTENT_02.getCode())
                        .contentBaseMonth("8")
                        .sendTypeCode(CampaignSendCycle.SEND_CY_02.getCode())
                        .sendStartDate(LocalDate.now().plusDays(1))
                        .sendEndDate(LocalDate.now().plusDays(1))
                        .sendDateTime("11:59")
                        .sendIterationTypeCode("DAY")
                        .sendIterationDay("1")
                        .build();

        // when
        marketingMessageService.campaignInfoSave(campaignDto);
        List<CampaignEntity> campaignEntities = campaignRepository.findAll();
        int maxValue = campaignEntities.stream().mapToInt(x -> Math.toIntExact(x.getCampaignNo())).max().orElseThrow(NoSuchElementException::new);
        Optional<CampaignEntity> campaignEntity = campaignRepository.findById((long) maxValue);

        campaignEntities.stream().forEach(item ->{
            System.out.println(item.toString());
        });

        // then
        assertAll(
                () -> assertNotNull(campaignEntity.get()),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getBotId())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getCardTypeCode())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getColor())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getCampaignName())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getTargetTypeCode())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getTargetBaseMonth())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getSendTypeCode())),
                () -> assertTrue(CommonUtils.isDate(campaignEntity.get().getSendStartDate(),"yyyy-MM-dd")),
                () -> assertTrue(CommonUtils.isDate(campaignEntity.get().getSendEndDate(),"yyyy-MM-dd")),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getSendDateTime())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getSendIterationDay())),
                () -> assertTrue(StringUtils.hasLength(campaignEntity.get().getSendIterationDayWeek()))
        );
    }

    @Test
    @DisplayName("uuid set test")
    void campaignUUIDsetTest(){

        //given
        List<CampaignEntity> list = campaignRepository.findAll();

        //when
        list.stream().forEach(item ->{
            /*item.setTrackingCode(marketingUtil.makeShortUUID());*/
            campaignRepository.save(item);
        });

        //then
        /*assertAll(
                () -> assertFalse(list.stream().parallel().filter(item -> ObjectUtils.isEmpty(item.getTrackingCode())).findFirst().isPresent())
        );*/
    }

    @Test
    void test(){
        try {
            MarketingResultDetailDto marketingResultDetailDto = marketingMessageService.getResultDetail(20);
            System.out.println(marketingResultDetailDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
