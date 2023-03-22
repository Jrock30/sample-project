package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.reponse.CampaignListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.RequestCampaignListUpdateDto;
import com.sample.project.api.marketingMessage.dto.request.SearchCampaignListDto;
import com.sample.project.api.marketingMessage.enums.CampaignProgressStatus;
import com.sample.project.api.marketingMessage.util.MarketingUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = projectFoApiApplication.class)
public class CampaignListTest {

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;

    @Autowired
    private MarketingUtil marketingUtil;

    @Test
    @DisplayName("캠페인 목록 조회 테스트 with QueryDsl")
    public void searchCampaignListTest(){
        // given
        SearchCampaignListDto searchCampaignListDto =
                SearchCampaignListDto.builder()
                        .campaignName("캠페인 테스트_02")
                        /*.periodType("PERIOD_TYPE_01")
                        .sendStartDate(LocalDate.now())
                        .sendEndDate(marketingUtil.convertLocaldate("2022-11","end"))//11/30
                        .campaignProgessStatus(CampaignProgressStatus.CAMP_ST_01.getCode())
                        .testSendYn("Y")*/
                        .build();
        // page num, 페이지에 보여줄 row 수,
        /*Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "campaignNo");*/
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "campaignNo"));

        // when
        Page<CampaignListResponseDto> campaignList = apple24MarketingMessageSub.searchCampaignList(searchCampaignListDto, pageable);
        campaignList.stream().forEach(item ->{
            System.out.println("######campaignList Test####: "+item);
        });

        // then
        assertAll(
                () -> assertTrue(campaignList.getSize()>0)
        );

    }

    @Test
    @DisplayName("진행 상태 코드 처리 테스트")
    public void progressStatusProcessTest(){
        //given

        RequestCampaignListUpdateDto requestCampaignListUpdateDto = new RequestCampaignListUpdateDto();
        requestCampaignListUpdateDto.setCampaignProgessStatus(CampaignProgressStatus.CAMP_ST_01.getCode());
        boolean result = false;
        Long campaignNo = 34L;

        //when

        try{
            apple24MarketingMessageSub.campaignProgressStatusProcess(campaignNo, requestCampaignListUpdateDto);
            result = true;
        }catch (Exception e){
            System.out.println(e.getCause());
        }

        //then
        boolean finalResult = result;
        assertAll(
                () -> assertTrue(finalResult)
        );
    }
}
