package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.MarketingMessageDto;
import com.sample.project.api.marketingMessage.dto.request.RequestMarketingMessageDto;
import com.sample.project.api.marketingMessage.repository.MarketingMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = projectFoApiApplication.class)
public class CampaignMessageSettingTest {

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;
    @Autowired
    private MarketingMessageRepository marketingMessageRepository;

    @Test
    @DisplayName("메시지 설정 목록 조회 테스트")
    void marketingMessageSettingTest(){
        //given
        String botId = "637c56ce8f7dc436c344fcbparker";

        //when
        List<MarketingMessageDto> marketingMessageDtoList = apple24MarketingMessageSub.getMarketingMessageList(botId);
        marketingMessageDtoList.stream().forEach(item -> {
            System.out.println("marketingMessageSettingTest : "+item);
        });

        //then
        assertAll(
                () -> assertEquals(2, marketingMessageDtoList.size())
        );
    }

    @Test
    @DisplayName("마케팅 메시지 초기 생성 기능 테스트")
    void marketingMessageInitDataSave(){
        //given
        RequestMarketingMessageDto requestMarketingMessageDto = new RequestMarketingMessageDto();
        requestMarketingMessageDto.setBotId("638db2cba7a5ab3119e2ff25");

        //when
        apple24MarketingMessageSub.campaignMessageInitSettingSave(requestMarketingMessageDto);

        //then
        assertAll(
                ()  -> assertTrue(marketingMessageRepository.findAll().stream().filter(item -> item.getBotId().equals("638db2cba7a5ab3119e2ff25")).collect(Collectors.toList()).size()>0)
        );
    }
}
