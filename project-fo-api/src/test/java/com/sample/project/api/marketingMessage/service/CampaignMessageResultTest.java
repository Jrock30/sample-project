package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.reponse.MarketingMessageResultListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.SearchMessageResultDto;
import com.sample.project.api.marketingMessage.enums.CampaignMessageResultPeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = projectFoApiApplication.class)
public class CampaignMessageResultTest {

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;

    @Test
    @DisplayName("마케팅 메시지 발송")
    void getMarketingMessageResultList(){
        // given
        // searchData
        SearchMessageResultDto searchMessageResultDto =
                SearchMessageResultDto.builder()
                        .botId("637c56ce8f7dc436c344fcbd")
                        .periodType(CampaignMessageResultPeriodType.RESULT_PERIOD_TYPE_02.getCode())
                        .calculateYear("2022").build();
        //page Info
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sendDate"));

        // when
        MarketingMessageResultListResponseDto marketingMessageResultListResponseDto =
                apple24MarketingMessageSub.getMarketingMessageResultList(searchMessageResultDto, pageable);

        //then
        assertAll(
                () -> assertTrue(marketingMessageResultListResponseDto.getMarketingMessageResultDtoPage().getTotalElements()>0)
        );

    }
}
