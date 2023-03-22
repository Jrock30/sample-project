package com.sample.project.api.apple.controller;

import com.sample.project.api.apple.dto.AdminAppleListDto;
import com.sample.project.api.apple.dto.request.RequestAppleAdminDelegateDto;
import com.sample.project.api.apple.dto.request.RequestAppleAdminFailDelegateDto;
import com.sample.project.api.apple.dto.request.RequestSearchBotListDto;
import com.sample.project.api.apple.service.AdminAppleService;
import com.sample.project.common.exception.RequestValidateException;
import com.sample.project.common.wrapper.CommonPageSuccessResponse;
import com.sample.project.common.wrapper.CommonSuccessResponse;
import com.sample.project.common.wrapper.Pagination;
import com.sample.project.view.ExcelXlsxView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/v1/admin/apple", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "관리자 챗봇", description = "관리자 챗봇 목록, 챗봇 상태 변경, 히스토리 등 API")
public class AdminAppleController {

    private final AdminAppleService adminAppleService;

    /**
     * @param    :
     * @screen   : 관리자 봇 목록 화면
     * @author   : user
     * @desc     : 관리자 봇 목록 조회
     * @since    : 2022/12/05
     */
    @GetMapping
    @Operation(
            summary = "관리자 봇 목록",
            description = "관리자 봇 목록 조회",
            parameters = {
            @Parameter(name = "startDate",         in = ParameterIn.QUERY, description = "기간 시작일"),
            @Parameter(name = "endDate",           in = ParameterIn.QUERY, description = "기간 종료일"),
            @Parameter(name = "dateTypeCode",      in = ParameterIn.QUERY, description = "기간 구분코드 " +
                    "{ BOT_SEARCH_DATE_TYPE_01: 선택안함" +
                    ", BOT_SEARCH_DATE_TYPE_02: 봇 생성일" +
                    ", BOT_SEARCH_DATE_TYPE_03: 위임-행 요청일" +
                    ", BOT_SEARCH_DATE_TYPE_04: 위임-대행 연결 성공일" +
                    ", BOT_SEARCH_DATE_TYPE_05: 위임-대행 연결 실패일" +
                    ", BOT_SEARCH_DATE_TYPE_06: 위임-대행 중지일}", example = "BOT_SEARCH_DATE_TYPE_01"),
            @Parameter(name = "searchText",        in = ParameterIn.QUERY, description = "검색어"),
            @Parameter(name = "searchTypeCode",    in = ParameterIn.QUERY, description = "검색어 구분코드 " +
                    "{ BOT_SEARCH_TYPE_01: 전체" +
                    ", BOT_SEARCH_TYPE_02: 봇 이름" +
                    ", BOT_SEARCH_TYPE_03: 봇 마스터 아이디" +
                    ", BOT_SEARCH_TYPE_04: 쇼핑몰 명" +
                    ", BOT_SEARCH_TYPE_05: 카페24 ID" +
                    ", BOT_SEARCH_TYPE_06: (위임)봇 ID" +
                    ", BOT_SEARCH_TYPE_07: 봇 마스터 연락처" +
                    ", BOT_SEARCH_TYPE_08: Admin Key" +
                    ", BOT_SEARCH_TYPE_09: 카카오채널 검색용 ID}",  example = "BOT_SEARCH_TYPE_01"),
            @Parameter(name = "delegationStatus",  in = ParameterIn.QUERY, description = "봇 상태 값(문자열 , separator 사용) " +
                    "{ BEFORE: 위임-대행 전" +
                    ", REQUEST: 위임-대행 요청 중" +
                    ", UNDER: 위임-대행 중" +
                    ", FAIL_NOT_APP: 위임-대행 요청 실패(앱 관리자 미 초대) " +
                    ", FAIL_NOT_ADMIN: 위임-대행 요청 실패(채널관리자 미 초대) " +
                    ", FAIL_SYSTEM: 위임-대행 요청 실패(시스템 오류)" +
                    ", STOP_NOT_PAY: 위임-대행 중지(요청)" +
                    ", STOP_REQUEST: 위임-대행 중지(미결제)" +
                    ", DELETE_ADMIN: 관리자 삭제" +
                    "}",  example = "BEFORE,REQUEST,UNDER,FAIL_NOT_APP,FAIL_NOT_ADMIN,FAIL_SYSTEM,STOP_NOT_PAY,STOP_REQUEST,DELETE_ADMIN"),
            @Parameter(name = "page",              in = ParameterIn.QUERY, description = "페이지 번호",          example = "0"),
            @Parameter(name = "size",              in = ParameterIn.QUERY, description = "페이지 목록 사이즈",    example = "10"),
//                    @Parameter(name = "sort",           in = ParameterIn.QUERY, description = "정렬 조건",            example = "regDt,desc"),
    }
    )
    public CommonPageSuccessResponse<List<AdminAppleListDto>> searchBotList(RequestSearchBotListDto requestSearchBotListDto, Pageable pageable) {
        Page<AdminAppleListDto> pageInfo = adminAppleService.searchBotList(requestSearchBotListDto, pageable);
        return new CommonPageSuccessResponse<>(pageInfo.getContent(), new Pagination(pageInfo));
    }

}
