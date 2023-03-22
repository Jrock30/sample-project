package com.sample.project.api.marketingMessage.controller;


import com.sample.project.api.marketingMessage.dto.*;
import com.sample.project.api.marketingMessage.dto.reponse.CampaignListResponseDto;
import com.sample.project.api.marketingMessage.dto.reponse.MarketingResultDetailDto;
import com.sample.project.api.marketingMessage.dto.request.*;
import com.sample.project.api.marketingMessage.service.MarketingMessageService;
import com.sample.project.common.exception.DefaultException;
import com.sample.project.common.exception.RequestValidateException;
import com.sample.project.common.wrapper.CommonMultipleTypePageSuccessResponse;
import com.sample.project.common.wrapper.CommonPageSuccessResponse;
import com.sample.project.common.wrapper.CommonSuccessResponse;
import com.sample.project.view.ExcelXlsxView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Tag(name = "마케팅 메시지", description = "마케팅 메시지 관련 API")
@Slf4j
@RestController
@RequestMapping(value = "/v1/user/marketingmessage")
@RequiredArgsConstructor
public class MarketingMessageController {

    private final MarketingMessageService marketingMessageService;

    /**
     * @param : CampaignDto
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 일정 >캠페인 추가
     * @since : 2022/11/23
     */
    @PostMapping("/campaign")
    @Operation(summary = "캠페인 일정 추가", description = "캠페인 일정 추가, 수정 API")
    public CommonSuccessResponse<?> saveCampaignInfo(@RequestBody CampaignDto campaignDto) {
        marketingMessageService.sendEventApi(marketingMessageService.campaignInfoSave(campaignDto));
        return new CommonSuccessResponse<>();
    }


    /**
     * @param : CampaignDto
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 일정 >캠페인 상세 조회
     * @since : 2022/11/28
     */
    @GetMapping("/campaign/{campaignNo}")
    @Operation(summary = "캠페인 일정 상세 조회", description = "캠페인 번호 기반 캠페인 일정 상세 조회 API")
    public CommonSuccessResponse<CampaignDto> getCampaignDeatilInfo(@PathVariable("campaignNo") Long campaignNo) {
        return new CommonSuccessResponse<>(marketingMessageService.getCampaignDeatilInfo(campaignNo));
    }


    /**
     * @param : String(baseMonth)
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 일정의 목록
     * @since : 2022/11/23
     */
    @GetMapping("/campaign/schedule/list")
    @Operation(summary = "캠페인 일정 목록 조회", description = "등록된 캠페인 일정 목록 조회 API")
    public CommonSuccessResponse<List<CampaignDto>> getCampaignScheduleList(@ModelAttribute RequestCampaignScheduleDto requestCampaignScheduleDto) throws DefaultException {
        return new CommonSuccessResponse<>(marketingMessageService.getCampaignScheduleList(requestCampaignScheduleDto));
    }

    /**
     * @param :
     * @author : user
     * @desc : 공휴일 등록
     * @since : 2022/11/24
     */
    @PostMapping("/holiday")
    @Operation(summary = "공휴일 등록", description = "공휴일 등록 기능")
    public CommonSuccessResponse<?> saveHolidayInfo(@RequestBody @Validated HolidayDto holidayDto, Errors errors) {
        if (errors.hasErrors()) {
            throw new RequestValidateException(errors);
        }
        marketingMessageService.saveHolidayInfo(holidayDto);
        return new CommonSuccessResponse<>();
    }

    /**
     * @param :
     * @author : user
     * @desc : 공휴일 조회
     * @since : 2022/11/25
     */
    @GetMapping("/holiday")
    @Operation(summary = "공휴일 조회", description = "등록되어 있는 공휴일 전체 조회")
    public CommonSuccessResponse<List<HolidayDto>> getHolidayList() {
        return new CommonSuccessResponse<>(marketingMessageService.getHolidayList());
    }

    /**
     * @param : searchCampaignDto
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 목록 조회
     * @since : 2022/11/23
     */
    @GetMapping("/campaign/list")
    @Operation(summary = "캠페인 목록 조회", description = "캠페인 목록 조회 API",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호", example = "1"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지 목록 사이즈", example = "10"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬 조건(sendStartDate:발송 주기 시작일, sendEndDate:발송 주기 종료일, DESC(내림차순),ASC(올림차순))", example = "sendStartDate,DESC"),
                    @Parameter(name = "botId", in = ParameterIn.QUERY, description = "botId", example = "637c56ce8f7dc436c344fcbd"),
                    @Parameter(name = "campaignName", in = ParameterIn.QUERY, description = "캠페인 명", example = "캠페인"),
                    @Parameter(name = "periodType", in = ParameterIn.QUERY, description = "기간 타입 코드(PERIOD_TYPE_01:시작 일자, PERIOD_TYPE_02:종료 일자, null로 보내주시면 all처리)", example = "PERIOD_TYPE_01"),
                    @Parameter(name = "startDate", in = ParameterIn.QUERY, description = "검색 조건 시작일", example = "2022-10-01"),
                    @Parameter(name = "endDate", in = ParameterIn.QUERY, description = "검색 조건 종료일", example = "2022-11-23"),
                    @Parameter(name = "campaignProgessStatus", in = ParameterIn.QUERY, description = "캠페인 진행 상태 코드(CAMP_ST_01:진행 예정, CAMP_ST_02:진행 중, CAMP_ST_03: 진행 중지, CAMP_ST_04:진행 완료, null로 보내주시면 all처리)", example = "CAMP_ST_01"),
                    @Parameter(name = "testSendYn", in = ParameterIn.QUERY, description = "테스트 발송 건 제외 여부(Y:테스트 발송 타입 제외, N:테스트 발송 타입 포함)", example = "Y")})
    public CommonPageSuccessResponse<List<CampaignListResponseDto>> searchCampaignList(@ModelAttribute SearchCampaignListDto searchCampaignListDto, Pageable pageable) {
        return marketingMessageService.searchCampaignList(searchCampaignListDto, pageable);
    }

    /**
     * @param : String(baseMonth)
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 일정의 목록 진행 상태 처리 API
     * @since : 2022/11/28
     */
    @PutMapping("/campaign/list/progressStatus/{campaignNo}")
    @Operation(summary = "캠페인 일정의 목록 진행 상태 처리", description = "캠페인 목록의 진행 상태 처리 API")
    CommonSuccessResponse<?> campaignProgressStatusProcess(@PathVariable("campaignNo") Long campaignNo, @RequestBody RequestCampaignListUpdateDto requestCampaignListUpdateDto) {
        marketingMessageService.campaignProgressStatusProcess(campaignNo, requestCampaignListUpdateDto);
        return new CommonSuccessResponse<>();
    }

    /**
     * @param : String(baseMonth)
     * @author : user
     * @desc : 마케팅 메시지 > 캠페인 일정의 목록 삭제 처리
     * @since : 2022/11/30
     */

    @DeleteMapping("/campaign/list/progressStatus/{campaignNo}")
    @Operation(summary = "캠페인 일정의 목록 삭제 처리", description = "캠페인 목록 삭제 처리 API")
    CommonSuccessResponse<?> campaignDeleteProcess(@PathVariable("campaignNo") Long campaignNo) {
        marketingMessageService.campaignDeleteProcess(campaignNo);
        return new CommonSuccessResponse<>();
    }

    /**
     * @param :
     * @author : user
     * @desc : 메시지 설정 단건 조회
     * @since : 2022/12/20
     */
    @GetMapping(value = "/message/setting/detail/{messageNo}")
    @Operation(summary = "메시지 설정 단건 조회", description = "마케팅 메시지의 메시지 설정 단건 조회")
    public CommonSuccessResponse<MarketingMessageDto> messageSettingUpdate(@PathVariable("messageNo") Long messageNo) {
        return new CommonSuccessResponse<>(marketingMessageService.getCampaignMessageSettingDetailInfo(messageNo));
    }

    /**
     * @param :
     * @author : user
     * @desc : 메시지 설정 수정
     * @since : 2022/11/29
     */
    @PutMapping(value = "/message/setting/{messageNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "메시지 설정 수정", description = "마케팅 메시지의 메시지에 대한 설정")
    public CommonSuccessResponse<MarketingMessageDto> messageSettingUpdate(
            @RequestPart String requestMarketingMessageString,
            @RequestPart(required = false) MultipartFile carouselFile,
            @PathVariable("messageNo") Long messageNo) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RequestMarketingMessageDto requestMarketingMessageDto = objectMapper.readValue(new String(requestMarketingMessageString.getBytes("8859_1"), "UTF-8"), RequestMarketingMessageDto.class);
        requestMarketingMessageDto.setMessageNo(messageNo);
        return new CommonSuccessResponse<>(marketingMessageService.campaignMessageSettingSave(requestMarketingMessageDto, carouselFile));
    }

    /**
     * @param :
     * @author : user
     * @desc : 메시지 설정 데이터 조회
     * @since : 2022/11/29
     */

    @GetMapping("/message/setting/{botId}")
    @Operation(summary = "메시지 설정 데이터 조회", description = "botId 기준 메시지 설정 리스트 조회")
    public CommonSuccessResponse<List<MarketingMessageDto>> getCampaignMessageList(@PathVariable("botId") String botId) {
        return new CommonSuccessResponse<>(marketingMessageService.getCampaignMessageList(botId));
    }

    /**
     * @param :
     * @author : user
     * @desc : 메시지 발송 결과 조회
     * @since : 2022/12/1
     */
    @GetMapping("/message/reulst")
    @Operation(summary = "메시지 발송 결과 조회", description = "메시지 발송 결과 조회 API",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호", example = "1"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지 목록 사이즈", example = "10"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬 조건(sendDate:발송일시,requestCnt:요청건수,successCnt:성공 건수,failCnt:실패건수,cost:비용,clickCnt:클릭 수,clickRate:클릭율,buyCnt:구매수,buyAmount:구매액,roas:ROAS, cpa:CPA    , DESC(내림차순),ASC(올림차순))", example = "sendDate,DESC"),
                    @Parameter(name = "botId", in = ParameterIn.QUERY, description = "botId", example = "63971905ffd9367c3e445cee"),
                    @Parameter(name = "mallId", in = ParameterIn.QUERY, description = "mallId(정산관리에서 상세내역으로 이동하기 시 사용)", example = "knworksbot"),
                    @Parameter(name = "periodType", in = ParameterIn.QUERY, description = "기간 타입 코드(RESULT_PERIOD_TYPE_01:발송 일시,RESULT_PERIOD_TYPE_02:정산 월, null로 보내주시면 all처리)", example = "RESULT_PERIOD_TYPE_01"),
                    @Parameter(name = "startDate", in = ParameterIn.QUERY, description = "검색 조건 시작일", example = "2022-12-01"),
                    @Parameter(name = "endDate", in = ParameterIn.QUERY, description = "검색 조건 종료일", example = "2022-12-30"),
                    @Parameter(name = "calculateYear", in = ParameterIn.QUERY, description = "정산월 연도", example = "2022"),
                    @Parameter(name = "calculateMonth", in = ParameterIn.QUERY, description = "정산월 월", example = "12"),
                    @Parameter(name = "campaignName", in = ParameterIn.QUERY, description = "캠페인 명", example = "캠페인")})
    public CommonMultipleTypePageSuccessResponse<List<MarketingMessageResultDto>, MarketingMessageResultOverviewDto> searchMarketingMessageResultList(@ModelAttribute SearchMessageResultDto searchMessageResultDto, Pageable pageable) {
        return marketingMessageService.searchMarketingMessageResultList(searchMessageResultDto, pageable);
    }


    /**
     * @param : searchMessageResultDto
     * @author : user
     * @desc : 메시지 발송 결과 조회 엑셀 데이터 다운로드
     * @since : 2022/12/22
     */
    @GetMapping("/message/reulst/download")
    @Operation(summary = "메시지 발송 결과 조회 엑셀 데이터 다운로드", description = "메시지 발송 결과 조회 엑셀 다운로드 API",
            parameters = {
                    @Parameter(name = "botId", in = ParameterIn.QUERY, description = "botId", example = "63971905ffd9367c3e445cee"),
                    @Parameter(name = "periodType", in = ParameterIn.QUERY, description = "기간 타입 코드(RESULT_PERIOD_TYPE_01:발송 일시,RESULT_PERIOD_TYPE_02:정산 월, null로 보내주시면 all처리)", example = "RESULT_PERIOD_TYPE_01"),
                    @Parameter(name = "startDate", in = ParameterIn.QUERY, description = "검색 조건 시작일", example = "2022-12-01"),
                    @Parameter(name = "endDate", in = ParameterIn.QUERY, description = "검색 조건 종료일", example = "2022-12-30"),
                    @Parameter(name = "calculateYear", in = ParameterIn.QUERY, description = "정산월 연도", example = "2022"),
                    @Parameter(name = "calculateMonth", in = ParameterIn.QUERY, description = "정산월 월", example = "12"),
                    @Parameter(name = "campaignName", in = ParameterIn.QUERY, description = "캠페인 명", example = "캠페인")})
    public ModelAndView downloadExel(@ModelAttribute SearchMessageResultDto searchMessageResultDto) throws IOException {
        return new ModelAndView(new ExcelXlsxView(), marketingMessageService.downloadMessageResultExcel(searchMessageResultDto));
    }

    /**
     * @param : messageResultNo
     * @author : user
     * @desc : 캠페인 메세지 발송결과 조회 팝업창
     * @since : 2022/12/22
     */
    @GetMapping("/result-popup/{messageResultNo}")
    @Operation(summary = "캠페인 메세지 발송결과 조회 팝업창", description = "캠페인 메세지 발송결과 조회 팝업창"
    )
    public CommonSuccessResponse<MarketingResultDetailDto> getResultDetail(
            @Parameter(name = "messageResultNo", description = "캠페인 식별번호") @PathVariable("messageResultNo") Long messageResultNo) throws IOException {
        return new CommonSuccessResponse<>(marketingMessageService.getResultDetail(messageResultNo));
    }

    /**
     * @author : user
     * @desc : 정산관리 엑셀 다운로드, 프로토타입
     * @since : 2022/12/29
     */
    @GetMapping("/settlement/manage/download/{settlementManagementNo}")
    public ModelAndView settlementDownloadExcel(@PathVariable("settlementManagementNo") Long settlementManagementNo) {
        return new ModelAndView(new ExcelXlsxView(), marketingMessageService.settlementExcelDownLoad(settlementManagementNo));
    }

}
