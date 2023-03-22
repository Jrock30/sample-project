package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.SettlementManagementDto;
import com.sample.project.api.marketingMessage.dto.reponse.MarketingMessageResultListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.SearchMessageResultDto;
import com.sample.project.api.marketingMessage.dto.request.SearchSettlementDto;
import com.sample.project.api.marketingMessage.repository.SettlementManagementRepository;
import com.sample.project.view.ExcelConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = projectFoApiApplication.class)
public class SettlementManagementTest {
    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;
    @Autowired
    private SettlementManagementRepository settlementManagementRepository;

    @Test
    @DisplayName("정산관리 목록 조회 기능 테스트(프로토 타입)")
    void searchSettlementManagementList(){
        //given
        SearchSettlementDto searchSettlementDto =
                SearchSettlementDto.builder()
                        .periodYear("2022")
                        .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "seq"));

        //when
        Page<SettlementManagementDto> settlementManagementDtos =
        apple24MarketingMessageSub.searchSettlementManagementList(searchSettlementDto, pageable);
        settlementManagementDtos.forEach(System.out::println);

        //then
        assertAll(
                () -> assertTrue(settlementManagementDtos.getSize()>0)
        );
    }

    @Test
    @DisplayName("정산관리 상세내역 보기 테스트")
    void getSettlementDetailInfo(){
        // given
        SearchMessageResultDto searchMessageResultDto =
                SearchMessageResultDto.builder()
                        .mallId("knworksbot")
                        .periodType("RESULT_PERIOD_TYPE_02")
                        .calculateYear("2022")// 종료일의 년도
                        .calculateMonth("12")// 종료일의 월
                        .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sendDate"));

        // when
        MarketingMessageResultListResponseDto messageResultListResponseDto =
                apple24MarketingMessageSub.getMarketingMessageResultList(searchMessageResultDto, pageable);

        //then
        assertAll(
                () -> assertTrue(!ObjectUtils.isEmpty(messageResultListResponseDto.getMarketingMessageResultOverviewDto().getTotalCnt())),
                () -> assertTrue(messageResultListResponseDto.getMarketingMessageResultDtoPage().getSize()>0)
        );
    }

    @Test
    @DisplayName("정산관리 엑셀 다운로드 테스트")
    void settlementExcelDownLoad(){
        // given
        SettlementManagementDto settlementManagementDto = new SettlementManagementDto(settlementManagementRepository.findById(31L).orElse(null));
        // when


        // then
    }

    private Map<String, Object> getCodesMap(SettlementManagementDto settlementManagementDto) {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "settlement_management_excel");
        map.put(ExcelConstant.HEAD, Arrays.asList(
                "정산명"
                ,"마감일"
                ,"정산기간"
                ,"총메시지 발송 수수료 금액(VAT 포함)"
                ,"총 발송 건수"
                ,"총 판매 수수료 금액(VAT 포함)"
                ,"총 판매 건수"
                ,"총 판매 금액"
                ,"가감 금액"
                ,"총 정산 금액(VAT 포함)"
                ,"결제상태"));
        List<String> detailList = new ArrayList<>();
        detailList.add(settlementManagementDto.getMallName());
        detailList.add(ObjectUtils.isEmpty(settlementManagementDto.getEndDate())?null:settlementManagementDto.getEndDate().toString());
        detailList.add(settlementManagementDto.getTotalSendVatAmount().toString());
        detailList.add(settlementManagementDto.getTotalSendCnt().toString());
        detailList.add(settlementManagementDto.getTotalSellVatAmount().toString());
        detailList.add(settlementManagementDto.getTotalSellCnt().toString());
        detailList.add(settlementManagementDto.getTotalSellAmount().toString());
        detailList.add(settlementManagementDto.getAddSubtractionAmount().toString());
        detailList.add(settlementManagementDto.getTotalSettlementAmount().toString());
        detailList.add(settlementManagementDto.getPaymentStatus());
        map.put(ExcelConstant.BODY, detailList);
        return map;
    }
}
