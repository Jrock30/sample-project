package com.sample.project.api.marketingMessage.service;

import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.*;
import com.sample.project.api.marketingMessage.dto.reponse.*;
import com.sample.project.api.marketingMessage.dto.request.*;
import com.sample.project.api.marketingMessage.entity.MarketingMessageResultEntity;
import com.sample.project.api.marketingMessage.entity.SettlementManagementEntity;
import com.sample.project.api.marketingMessage.enums.CampaignMessageResultPeriodType;
import com.sample.project.api.marketingMessage.enums.CampaignSendCycle;
import com.sample.project.api.marketingMessage.enums.SendIterrationType;
import com.sample.project.api.marketingMessage.repository.MarketingMessageResultRepository;
import com.sample.project.api.marketingMessage.repository.SettlementManagementRepository;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.exception.DefaultException;
import com.sample.project.common.exception.DefaultRuntimeException;
import com.sample.project.common.service.web.WebClientService;
import com.sample.project.common.wrapper.CommonMultipleTypePageSuccessResponse;
import com.sample.project.common.wrapper.CommonPageSuccessResponse;
import com.sample.project.common.wrapper.Pagination;
import com.sample.project.view.ExcelConstant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_4000;
import static com.sample.project.common.type.ResponseErrorCode.FAIL_7022;

/**
 * @author : user
 * @desc : 마케팅 메시지 서비스
 * @since : 2022/11/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarketingMessageService {
    private final ObjectMapper objectMapper;
    private final MarketingMessageResultRepository marketingMessageResultRepository;
    private final SettlementManagementRepository settlementManagementRepository;
    private final Apple24MarketingMessageSub apple24MarketingMessageSub;
    private final WebClientService webClientService;
    private final AppleRepository appleRepository;

    /**
     * 마케팅 메시지>캠페인 추가 메소드
     */
    public CampaignDto campaignInfoSave(CampaignDto campaignDto) {
        if (apple24MarketingMessageSub.campaignAddDtoValidator(campaignDto)) {
            // 반복 주기 설정이 반복 발송이고, 반복주기가 주간 일 경우
            if (!ObjectUtils.isEmpty(campaignDto.getSendTypeCode()) && campaignDto.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_02.getCode()) &&
                    campaignDto.getSendIterationTypeCode().equals(SendIterrationType.WEEK.getCode())) {
                campaignDto.setSendIterationDayWeek(apple24MarketingMessageSub.sendIterationDayWeekListToString(campaignDto.getSendIterationDayWeekList()));
            }
            campaignDto.setColor(campaignDto.getBackGroundColor().getColorCode());
            campaignDto.setCampaignNo(apple24MarketingMessageSub.saveCampaignInfo(campaignDto));
            return campaignDto;
        } else {
            throw new DefaultRuntimeException(FAIL_4000, FAIL_4000.message());
        }
    }

    public void sendEventApi(CampaignDto campaignDto) {
        apple24MarketingMessageSub.sendEventApi(campaignDto);
    }

    /**
     * 마케팅 메시지>캠페인 상세 조회
     */
    public CampaignDto getCampaignDeatilInfo(Long campaignNo) {
        return apple24MarketingMessageSub.getCampaignDeatilInfo(campaignNo);
    }


    /**
     * 마케팅 메시지>캠페인 일정 목록 추가
     */
    public List<CampaignDto> getCampaignScheduleList(RequestCampaignScheduleDto requestCampaignScheduleDto) throws DefaultException {
        return apple24MarketingMessageSub.getCampaignScheduleList(requestCampaignScheduleDto);
    }

    /**
     * 마케팅 메시지 목록 조회
     */
    public CommonPageSuccessResponse<List<CampaignListResponseDto>> searchCampaignList(SearchCampaignListDto searchCampaignListDto, Pageable pageable) {
        Page<CampaignListResponseDto> pageInfo = apple24MarketingMessageSub.searchCampaignList(searchCampaignListDto, pageable);
        Pagination pagination = apple24MarketingMessageSub.makePagenation(pageInfo);
        return new CommonPageSuccessResponse<>(pageInfo.getContent(), pagination);
    }

    /**
     * 마케팅 메시지 > 캠페인 일정 목록 진행 상태 처리 메소드
     */
    public void campaignProgressStatusProcess(Long campaignNo, RequestCampaignListUpdateDto requestCampaignListUpdateDto) {
        apple24MarketingMessageSub.campaignProgressStatusProcess(campaignNo, requestCampaignListUpdateDto);
    }

    /**
     * 마케팅 메시지 > 캠페인 일정 목록 삭제 처리
     */
    public void campaignDeleteProcess(Long campaignNo) {
        apple24MarketingMessageSub.campaignDeleteProcess(campaignNo);
    }


    /**
     * 공휴일 등록
     */
    public void saveHolidayInfo(HolidayDto holidayDto) {
        apple24MarketingMessageSub.saveHolidayInfo(holidayDto);
    }

    /**
     * 공휴일 조회
     */
    public List<HolidayDto> getHolidayList() {
        return apple24MarketingMessageSub.getHolidayList();
    }

    /*
     * 마케팅 메시지>메시지 설정
     * */
    public MarketingMessageDto campaignMessageSettingSave(RequestMarketingMessageDto requestMarketingMessageDto, MultipartFile carouselFile) {
        return apple24MarketingMessageSub.campaignMessageSettingSave(requestMarketingMessageDto, carouselFile);
    }

    /*
     * 마케팅 메시지>메시지 단건 조회
     * */
    public MarketingMessageDto getCampaignMessageSettingDetailInfo(Long messageNo) {
        return apple24MarketingMessageSub.getCampaignMessageSettingDetailInfo(messageNo);
    }

    /*
     * 마케팅 메시지>메시지 설정 목록 조회
     * */
    public List<MarketingMessageDto> getCampaignMessageList(String botId) {
        return apple24MarketingMessageSub.getMarketingMessageList(botId);
    }

    /**
     * 마케팅 메시지 목록 조회
     */
    public CommonMultipleTypePageSuccessResponse<List<MarketingMessageResultDto>, MarketingMessageResultOverviewDto> searchMarketingMessageResultList(SearchMessageResultDto searchMessageResultDto, Pageable pageable) {
        MarketingMessageResultListResponseDto marketingMessageResultListResponseDto = apple24MarketingMessageSub.getMarketingMessageResultList(searchMessageResultDto, pageable);
        marketingMessageResultListResponseDto.setMarketingMessageResultDtoPage(this.makeCreateMessageSendResultDay(marketingMessageResultListResponseDto.getMarketingMessageResultDtoPage()));

        Pagination pagination = apple24MarketingMessageSub.makePagenation(marketingMessageResultListResponseDto.getMarketingMessageResultDtoPage());
        MarketingMessageResultOverviewDto marketingMessageResultOverviewObject = marketingMessageResultListResponseDto.getMarketingMessageResultOverviewDto();
        return new CommonMultipleTypePageSuccessResponse<>(marketingMessageResultListResponseDto.getMarketingMessageResultDtoPage().getContent(), pagination, marketingMessageResultOverviewObject);
    }

    /**
     * 마케팅 메세지 조회결과 팝업
     */
    public MarketingResultDetailDto getResultDetail(long messageResultNo) throws IOException {

        // 메세지발송경과번호로 세부사항 정보 조회(캠페인명, 구매수, 발송일자, 구매액...)
        MarketingMessageResultEntity marketingMessageResultInfo = marketingMessageResultRepository.findById(messageResultNo).orElseThrow(() -> new CustomException(FAIL_7022.message(), HttpStatus.BAD_REQUEST));
        // api로 인플로우 코드로 집계된 구매 목록 조회
        WebClientUrlCond urlPathParam;

        urlPathParam = WebClientUrlCond.builder()
                .lastPath("/admin/orders")
                .startDate(marketingMessageResultInfo.getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .endDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .embed("items")
                .inflowPath(String.valueOf(marketingMessageResultInfo.getMessageSendId()))
                .limit("500")
                .build();

        String mallId = appleRepository.findByBotId(marketingMessageResultInfo.getBotId()).getMallId();
        List<SendResultDetailDto> resultDetailList = objectMapper.readValue(webClientService.getApple24JsonAuthPeriod(mallId, urlPathParam).get("orders").traverse(), new TypeReference<>() {
        });

        // 보내줘야하는 응답 Items리스트
        List<ResponseItems> resultItemsList = new ArrayList<>();

        int quantity = 0;

        // 조회한 리스트 값 넣어서 보내주기
        for (SendResultDetailDto resultDetailDto : resultDetailList) {

            // orderDate 형식 맞추기 2022-12-19T13:43:00+09:00 => 2022-12-19 13:43
            // 조회결과 Items리스트
            List<Items> itemsDto = resultDetailDto.getItems();
            for (Items item : itemsDto) {
                String orderDate = item.getOrderedDate().substring(0, 10) + " " + item.getOrderedDate().substring(11, 16);
                item.setOrderedDate(orderDate);
                resultItemsList.add(
                        ResponseItems.builder()
                                .productNo(item.getProductNo())
                                .orderedDate(item.getOrderedDate())
                                .orderItemCode(item.getOrderItemCode())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .build());
                quantity += item.getQuantity();
            }
        }

        return MarketingResultDetailDto.builder()
                .campaignName(marketingMessageResultInfo.getCampaignName())
                .quantity((long) quantity)
                .paymentAmount(marketingMessageResultInfo.getBuyAmount())
                .sendDate(marketingMessageResultInfo.getSendDate())
                .items(resultItemsList)
                .build();
    }

    /**
     * @param searchMessageResultDto
     * @return
     */
    public Map<String, Object> downloadMessageResultExcel(SearchMessageResultDto searchMessageResultDto) throws IOException {
        List<MarketingMessageResultDto> messageResultListNoPaging = apple24MarketingMessageSub.getMarketingMessageResultListNoPaging(searchMessageResultDto);
        List<MarketingDetailExcelDto> marketingExcelList = new ArrayList<>();

        for (MarketingMessageResultDto result : messageResultListNoPaging) {
            // 메시지 발송 결과 번호별 결과조회
            MarketingResultDetailDto resultDetail = getResultDetail(result.getMessageResultNo());
            List<ResponseItems> items = resultDetail.getItems();
            // 조회한 데이터를 엑셀에 넣어줄 데이터에 집어넣기
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    marketingExcelList.add(MarketingDetailExcelDto.builder()
                            .botId(result.getBotId())
                            .sendDate(result.getSendDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                            .sendDayCount(makeCreateMessageSendResultDay(result.getSendDate()))
                            .calculateMonth(result.getCalculateMonth())
                            .messageSendId(result.getMessageSendId())
                            .campaignName(result.getCampaignName())
                            .orderedDate(items.get(i).getOrderedDate())
                            .orderItemCode(items.get(i).getOrderItemCode())
                            .productName(items.get(i).getProductName())
                            .quantity(items.get(i).getQuantity())
                            .build());
                } else {
                    marketingExcelList.add(MarketingDetailExcelDto.builder()
                            .botId(result.getBotId())
                            .sendDate(result.getSendDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                            .sendDayCount(makeCreateMessageSendResultDay(result.getSendDate()))
                            .calculateMonth(result.getCalculateMonth())
                            .messageSendId(result.getMessageSendId())
                            .campaignName(result.getCampaignName())
                            .orderedDate(items.get(i).getOrderedDate())
                            .orderItemCode(items.get(i).getOrderItemCode())
                            .productName(items.get(i).getProductName())
                            .quantity(items.get(i).getQuantity())
                            .buyAmount(result.getBuyAmount())
                            .build());
                }

            }
        }

        return getCodesMap(marketingExcelList);
    }

    //refac 요함 무조건!!!
    public Map<String, Object> settlementExcelDownLoad(Long settlementManagementNo) {
        Map<String, Object> returnMap = new HashMap<>();
        List<Map<String, Object>> mapList = new ArrayList<>();

        SettlementManagementDto settlementManagementDto = new SettlementManagementDto(settlementManagementRepository.findById(settlementManagementNo).orElse(null));
        mapList.add(this.getCodesMap(settlementManagementDto));

        Optional<SettlementManagementEntity> optionalSettlementManagementEntity = settlementManagementRepository.findById(settlementManagementNo);
        if (optionalSettlementManagementEntity.isPresent()) {
            List<AppleEntity> appleEntityList = appleRepository.findByMallId(optionalSettlementManagementEntity.get().getMallId());
            appleEntityList.stream().findFirst().ifPresent(
                    item -> mapList.add(
                            this.getCodesMap1(
                                    apple24MarketingMessageSub.getMarketingMessageResultListNoPaging(
                                            SearchMessageResultDto.builder()
                                                    .botId(item.getBotId())
                                                    .mallId(item.getMallId())
                                                    .periodType(CampaignMessageResultPeriodType.RESULT_PERIOD_TYPE_02.getCode())
                                                    .calculateYear(String.valueOf(optionalSettlementManagementEntity.get().getEndDate().getYear()))
                                                    .calculateMonth(String.valueOf(optionalSettlementManagementEntity.get().getEndDate().getMonthValue()))
                                                    .build())
                            )
                    )
            );
        }
        returnMap.put("list", mapList);
        return returnMap;
    }

    ////////////////private///////////////////
    private String makeCreateMessageSendResultDay(LocalDate sendDate) {
        Period period = Period.between(sendDate, LocalDate.now());
        if (period.getDays() < 3) {
            return BigDecimal.valueOf(period.getDays()).add(BigDecimal.valueOf(1)).toString() + "일차";
        } else {
            return "확정";
        }
    }

    /**
     * 관리자 기본응답 관리
     * 엑셀 다운로드 매핑정보.
     */
    private Map<String, Object> getCodesMap(List<MarketingDetailExcelDto> list) {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "marketing_code_excel");
        map.put(ExcelConstant.SHEET, "메시지 발송 결과");
        map.put(ExcelConstant.HEAD, Arrays.asList(
                "봇 ID",
                "발송일시",
                "발송일차",
                "정산월",
                "메세지 ID",
                "캠페인명",
                "주문일자",
                "품주별 주문번호",
                "상품명",
                "수량",
                "결제금액"));

        List<List<String>> detailList = new ArrayList<>();
        List<String> data;
        for (MarketingDetailExcelDto code : list) {
            data = new ArrayList<>();
            data.add(code.getBotId());
            data.add(code.getSendDate());
            data.add(ObjectUtils.isEmpty(code.getSendDayCount()) ? "" : code.getSendDayCount());
            data.add(ObjectUtils.isEmpty(code.getCalculateMonth()) ? null : code.getCalculateMonth().format(DateTimeFormatter.ofPattern("yyyy.MM")));
            data.add(String.valueOf(code.getMessageSendId()));
            data.add(code.getCampaignName());
            data.add(code.getOrderedDate());
            data.add(code.getOrderItemCode());
            data.add(code.getProductName());
            data.add(String.valueOf(code.getQuantity()));
            data.add(String.valueOf(code.getBuyAmount()));
            detailList.add(data);
        }

        map.put(ExcelConstant.BODY, detailList);
        return map;
    }

    // 발송 일차 데이터 재가공 메소드
    private Page<MarketingMessageResultDto> makeCreateMessageSendResultDay(Page<MarketingMessageResultDto> pageInfoList) {
        pageInfoList.forEach(item -> {
            // 0,1,2,3
            Period period = Period.between(item.getSendDate(), LocalDate.now());
            if (period.getDays() < 3) {
                item.setSendDayCount(BigDecimal.valueOf(period.getDays()).add(BigDecimal.valueOf(1)).toString() + "일차");
            } else {
                item.setSendDayCount("확정");
            }
        });
        return pageInfoList;
    }

    // test Area start
    private Map<String, Object> getCodesMap(SettlementManagementDto settlementManagementDto) {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "settlement_management_excel");
        map.put(ExcelConstant.SHEET, "정산서");
        map.put(ExcelConstant.HEAD, Arrays.asList(
                "정산명"
                , "마감일"
                , "정산기간"
                , "총메시지 발송 수수료 금액(VAT 포함)"
                , "총 발송 건수"
                , "총 판매 수수료 금액(VAT 포함)"
                , "총 판매 건수"
                , "총 판매 금액"
                , "가감 금액"
                , "총 정산 금액(VAT 포함)"
                , "결제상태"));
        List<List<String>> datadetailList = new ArrayList<>();
        List<String> data = new ArrayList<>();
        data.add(this.settlementName());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getEndDate()) ? null : settlementManagementDto.getEndDate().toString());
        data.add(settlementManagementDto.getStartDate().toString() + "~" + settlementManagementDto.getEndDate());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSendVatAmount()) ? null : settlementManagementDto.getTotalSendVatAmount().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSendCnt()) ? null : settlementManagementDto.getTotalSendCnt().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSellVatAmount()) ? null : settlementManagementDto.getTotalSellVatAmount().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSellCnt()) ? null : settlementManagementDto.getTotalSellCnt().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSellAmount()) ? null : settlementManagementDto.getTotalSellAmount().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getAddSubtractionAmount()) ? null : settlementManagementDto.getAddSubtractionAmount().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getTotalSettlementAmount()) ? null : settlementManagementDto.getTotalSettlementAmount().toString());
        data.add(ObjectUtils.isEmpty(settlementManagementDto.getPaymentStatus()) ? null : settlementManagementDto.getPaymentStatus());
        datadetailList.add(data);
        map.put(ExcelConstant.BODY, datadetailList);
        return map;
    }

    // 정산명 생성 메소드
    private String settlementName() {
        return LocalDate.now().getYear() + "년 " + LocalDate.now().getMonthValue() + "월분 수수료";
    }


    private Map<String, Object> getCodesMap1(List<MarketingMessageResultDto> list) {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "marketing_code_excel");
        map.put(ExcelConstant.SHEET, "메시지 발송 결과");
        map.put(ExcelConstant.HEAD, Arrays.asList(
                "봇 ID",
                "발송일시",
                "발송일차",
                "정산월",
                "메세지 ID",
                "캠페인명",
                "발송요청",
                "발송성공",
                "발송비용",
                "발송실패",
                "클릭수",
                "클릭률",
                "구매수",
                "구매전환율",
                "구매액",
                "ROAS",
                "CPA"
                ));

        List<List<String>> detailList = new ArrayList<>();
        List<String> data;
        for (MarketingMessageResultDto code : list) {
            data = new ArrayList<>();
            data.add(code.getBotId());
            data.add(code.getSendDate().toString());
            data.add(ObjectUtils.isEmpty(code.getSendDayCount()) ? "" : code.getSendDayCount());
            data.add(ObjectUtils.isEmpty(code.getCalculateMonth()) ? null : code.getCalculateMonth().format(DateTimeFormatter.ofPattern("yyyy.MM")));
            data.add(String.valueOf(code.getMessageSendId()));
            data.add(code.getCampaignName());
            data.add(code.getRequestCnt().toString());
            data.add(code.getSuccessCnt().toString());
            data.add(code.getCost().toString());
            data.add(code.getFailCnt().toString());
            data.add(ObjectUtils.isEmpty(code.getClickCnt())?"":code.getClickCnt().toString());
            data.add(ObjectUtils.isEmpty(code.getClickRate())?"":code.getClickRate().toString());
            data.add(ObjectUtils.isEmpty(code.getBuyCnt())?"":code.getBuyCnt().toString());
            data.add(ObjectUtils.isEmpty(code.getBuyConvRate())?"":code.getBuyConvRate().toString());
            data.add(ObjectUtils.isEmpty(code.getBuyAmount())?"":code.getBuyAmount().toString());
            data.add(ObjectUtils.isEmpty(code.getRoas())?"":code.getRoas().toString());
            data.add(ObjectUtils.isEmpty(code.getCpa())?"":code.getCpa().toString());
            detailList.add(data);
        }

        map.put(ExcelConstant.BODY, detailList);
        return map;
    }


    // test Area end
}

























