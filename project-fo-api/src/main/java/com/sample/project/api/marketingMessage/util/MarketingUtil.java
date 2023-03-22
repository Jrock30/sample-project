package com.sample.project.api.marketingMessage.util;

import com.sample.project.api.marketingMessage.dto.AttachFileDto;
import com.sample.project.api.marketingMessage.dto.BackGroundColor;
import com.sample.project.api.marketingMessage.dto.CampaignDto;
import com.sample.project.api.marketingMessage.dto.MarketingMessageDto;
import com.sample.project.api.marketingMessage.dto.request.RequestMarketingMessageDto;
import com.sample.project.api.marketingMessage.entity.AttachFileEntity;
import com.sample.project.api.marketingMessage.enums.*;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.exception.DefaultRuntimeException;
import com.sample.project.common.service.storage.StorageService;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.wrapper.Pagination;
import com.sample.project.security.SecurityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author   	: user
 * @since    	: 2022/11/22
 * @desc     	: 마케팅 메시지에서만 사용되는 util
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketingUtil {

    @Value("${tenth2.service-id}")
    private String serviceId;

    @Value("${tenth2.agency-path}")
    private String apple24;
    private final ObjectMapper objectMapper;
    private final StorageService storageService;

    /**
     * @author   	: user
     * @since    	: 2022/11/22
     * @desc     	: 캠페인 DTO validator, refac예정
     */
    public boolean campaignAddDtoValidator(CampaignDto campaignDto){

        if(!StringUtils.hasLength(campaignDto.getCampaignName())) return false; // 캠페인 명 체크

        if(!StringUtils.hasLength(campaignDto.getBackGroundColor().getColorCode())) return false; // 색상 체크

        if(StringUtils.hasLength(campaignDto.getTargetTypeCode())){ // 타겟 설정 체크
            if(campaignDto.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())){//테스트 번호 타입 코드
                if(!StringUtils.hasLength(campaignDto.getTargetTestMobile())) return false;
            }else{
                if(!StringUtils.hasLength(campaignDto.getTargetBaseMonth())) return false;
            }
        }else return false;

        if(StringUtils.hasLength(campaignDto.getContentTypeCode())){ // 상품 설정 체크
            if(!campaignDto.getContentTypeCode().equals(CampaignContent.CONTENT_01.getCode()) &&
                    !StringUtils.hasLength(campaignDto.getContentBaseMonth())){// 신상품이 아닌 경우, 기준 월이 없을 경우
                return false;
            }
        }else return false;

        if(StringUtils.hasLength(campaignDto.getSendTypeCode()) && !campaignDto.getSendTypeCode().equals(CampaignTargetType.TARGET_01)){// 발송 주기 설정 체크, 테스트 타겟 타입일 경우 제외

            if(campaignDto.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_01.getCode())){//발송주기 설정 구분 코드 '한번만'
                try{
                    if(CommonUtils.isDate(campaignDto.getSendStartDate(), "yyyy-MM-dd")){
                        if(!StringUtils.hasLength(campaignDto.getSendDateTime())) {
                            return false;
                        }else {
                            //todo: 모든 값이 있는 경우 날짜 validation Check
                            return this.dateValidation(campaignDto);
                        }
                    }else return false;
                }catch (RuntimeException e){
                    log.debug("sendDate validation result fail -> {}", CommonUtils.getPrintStackTrace(e));
                    throw new DefaultRuntimeException(ResponseErrorCode.FAIL_4000, ResponseErrorCode.FAIL_4000.message());
                }

            }else{// 반복 발송
                try{
                    if(CommonUtils.isDate(campaignDto.getSendStartDate(), "yyyy-MM-dd")
                            && CommonUtils.isDate(campaignDto.getSendEndDate(), "yyyy-MM-dd")){

                        // 발송 주기 일, 발송 주기 타입 코드 (일, 주) 체크
                        if(!StringUtils.hasLength(campaignDto.getSendIterationDay())||!StringUtils.hasLength(campaignDto.getSendIterationTypeCode())) return false;
                        // 발송 주기 설정> 반복주기가 주 일 경우만 반복요일 리스트 체크
                        if(campaignDto.getSendIterationTypeCode().equals(SendIterrationType.WEEK.getCode()) && campaignDto.getSendIterationDayWeekList().size()==0) return false;
                        // sendTime 체크
                        if(!StringUtils.hasLength(campaignDto.getSendDateTime())) {
                            return false;
                        }else{
                            //todo: 모든 값이 있는 경우 날짜 validation Check
                            return this.dateValidation(campaignDto);
                        }
                    }else return false;
                } catch (RuntimeException e) {
                    log.debug("sendDate validation result fail -> {}", CommonUtils.getPrintStackTrace(e));
                    throw new DefaultRuntimeException(ResponseErrorCode.FAIL_4000, ResponseErrorCode.FAIL_4000.message());
                }
            }
        }

        if(!StringUtils.hasLength(campaignDto.getCardTypeCode())) return false; // 카드 타입 체크

        return true;
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/22
     * @desc     	: 마케팅 메시지>캠페인 일정 목록의 기준 월 String Data를 LocalDate 데이터 형태로 변환 해 주는 메소드
     */
    public LocalDate convertLocaldate(String baseMonth, String type){

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(baseMonth, dateTimeFormatter);

        if(type.equals("start")){
            return yearMonth.atDay(1); // choose whatever day you want
        }else{
            return yearMonth.atEndOfMonth();
        }
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/24
     * @desc     	: StringList를 String형태로 변환해주는 메소드 리스트형태의 데이터를 하나의 컬럼을 관리하고 싶을 때 사용
     */
    public String stringListToStringConvertor(List<String> targetString){
        StringBuilder stringBuilder = new StringBuilder();
        targetString.forEach(item ->{
            stringBuilder.append(item);
            stringBuilder.append("||");
        });
        return stringBuilder.toString();
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/24
     * @desc     	: 발송 반복 요일 String 형태를 CollectionList형태로 변환 해 주는 메소드 이것 또한 데이터 컴럼에 하나로 관리하기 위함
     */
    public List<String> sendIterationDayWeekToListConvertor(String sendIterationDayWeek){
        List<String> sendIterationDayWeekList = new ArrayList<>();
        String [] targetList = sendIterationDayWeek.split("\\|\\|");
        for(String item : targetList){
            if(com.sample.project.common.utils.StringUtils.hasLength(item)){
                sendIterationDayWeekList.add(item);
            }
        }
        return sendIterationDayWeekList;
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/24
     * @desc     	: 발송 반복 요일 String 형태를 CollectionList형태로 변환 해 주는 메소드 이것 또한 데이터 컴럼에 하나로 관리하기 위함
     */
    public BackGroundColor colorCodeToObjectConvertor(String colorCode){
        if(StringUtils.hasLength(colorCode)){
            colorCode= colorCode.replace("#","");
            return BackGroundColor.builder()
                    .colorCode(CampaignColor.valueOf(colorCode).code())
                    .colorName(CampaignColor.valueOf(colorCode).codeName())
                    .build();
        }else{
            return null;
        }
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/24
     * @desc     	: 마케팅 코드 to name convert
     */
    /*CampaignCode -> codeName 변환 */
    public CampaignDto getCampaignCodeName(CampaignDto campaignDto){
        // color -> backGroundColor로 변환
        campaignDto.setBackGroundColor(this.colorCodeToObjectConvertor(campaignDto.getColor()));
        // cardType -> name
        campaignDto.setCardTypeCodeName(CampaignCardType.valueOf(campaignDto.getCardTypeCode()).getCodeName());
        // targetTypeCode -> name
        campaignDto.setTargetTypeCodeName(CampaignTargetType.valueOf(campaignDto.getTargetTypeCode()).getCodeName());
        // contentTypeCode -> name
        campaignDto.setContentTypeCodeName(CampaignContent.valueOf(campaignDto.getContentTypeCode()).getCodeName());

        // sendCy -> name
        if(!ObjectUtils.isEmpty(campaignDto.getSendTypeCode())){
            campaignDto.setSendTypeCodeName(CampaignSendCycle.valueOf(campaignDto.getSendTypeCode()).getCodeName());
        }

        // campaignProgessStatus -> name
        campaignDto.setCampaignProgessStatusName(CampaignProgressStatus.valueOf(campaignDto.getCampaignProgessStatus()).getCodeName());
        // sendIterationTypecode -> name
        if(!ObjectUtils.isEmpty(campaignDto.getSendIterationTypeCode())){
            campaignDto.setSendIterationTypeCodeName(SendIterrationType.valueOf(campaignDto.getSendIterationTypeCode()).getCodeName());
        }

        return campaignDto;
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/28
     * @desc     	: 마케팅 메시지>메시지 설정 케러셀형 헤드 타이틀, 설명 기본 값 설정 기능
     */

    public MarketingMessageDto carouselDefaultValueSetting(MarketingMessageDto marketingMessageDto){
        String productTitle= new String();
        String productContent = new String();
        // 커머스 형
        if(marketingMessageDto.getCardTypeCode().equals(CampaignCardType.CARD_02.getCode())){
            if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_01.getCode())){
                productTitle = MaketingMessageDefault.NEW_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.NEW_PRODUCT_CONTENT.getCodeName();
            }else if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_02.getCode())){
                productTitle = MaketingMessageDefault.BUY_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.BUY_PRODUCT_CONTENT.getCodeName();
            }else if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_03.getCode())){
                productTitle = MaketingMessageDefault.REVIEW_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.REVIEW_PRODUCT_CONTENT.getCodeName();
            }
        }else{
            // 리스트 형
            if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_01.getCode())){
                productTitle = MaketingMessageDefault.LIST_NEW_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.LIST_NEW_PRODUCT_CONTENT.getCodeName();
            }else if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_02.getCode())){
                productTitle = MaketingMessageDefault.LIST_BUY_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.LIST_BUY_PRODUCT_CONTENT.getCodeName();
            }else if(marketingMessageDto.getCardDetailTypeCode().equals(CampaignCardDetailType.CARD_DTI_03.getCode())){
                productTitle = MaketingMessageDefault.LIST_REVIEW_PRODUCT_TITLE.getCodeName();
                productContent = MaketingMessageDefault.LIST_REVIEW_PRODUCT_CONTENT.getCodeName();
            }
        }


        // 상품 제목 체크 후 null이거나 빈값이면 기본 값 셋팅
        if(ObjectUtils.isEmpty(marketingMessageDto.getProductTitle()) || !StringUtils.hasLength(marketingMessageDto.getProductTitle())){
            marketingMessageDto.setProductTitle(productTitle);
        }

        // 상품 설명 체크
        if(ObjectUtils.isEmpty(marketingMessageDto.getProductContent()) || !StringUtils.hasLength(marketingMessageDto.getProductContent())){
            marketingMessageDto.setProductContent(productContent);
        }

        return marketingMessageDto;
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/29
     * @desc     	: 마케팅 메시지>메시지 설정 케러셀형 이미지 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public AttachFileDto saveCarouselFile(MultipartFile file){
        String savePath = "/" + serviceId + "/" + apple24 + "/" + SecurityUtils.getMallId();
        try {
            String result = storageService.saveFile(file, savePath);

            JsonNode rootNode = objectMapper.readTree(result);
            String path = rootNode.path("https").path("path").asText();
            String url = rootNode.path("https").path("original").asText();
            String thumbnailUrl = rootNode.path("https").path("image").asText();

            AttachFileEntity fileEntity = AttachFileEntity.builder()
                    .attachFileName(file.getOriginalFilename())
                    .pysicsFileName(com.sample.project.common.utils.StringUtils.getFilename(path))
                    .filePath(path)
                    .fileSize(file.getSize())
                    .fileUrl(url)
                    .thumbnailUrl(thumbnailUrl)
                    .regId(SecurityUtils.getCurrentUserId().orElse(null))
                    .regDate(LocalDateTime.now())
                    .updId(SecurityUtils.getCurrentUserId().orElse(null))
                    .updDate(LocalDateTime.now())
                    .build();

            return new AttachFileDto(fileEntity);

        } catch (Exception e) {
            throw  new CustomException(ResponseErrorCode.FAIL_500.message(), ResponseErrorCode.FAIL_500.status());
        }
    }

    /**
     * @author   	: user
     * @since    	: 2022/11/29
     * @desc     	: 마케팅 메시지>메시지 설정 케러셀형 헤드 타이틀, 설명 request값 설정 기능
     */
    public MarketingMessageDto setRequestCampaignMessageSettingData(RequestMarketingMessageDto requestMarketingMessageDto, MarketingMessageDto marketingMessageDto){
        // 메시지 번호 셋팅
        marketingMessageDto.setMessageNo(requestMarketingMessageDto.getMessageNo());
        // 상품 제목 셋팅
        marketingMessageDto.setProductTitle(ObjectUtils.isEmpty(requestMarketingMessageDto.getProductTitle())?null:requestMarketingMessageDto.getProductTitle());
        // 상품 내용 셋팅
        marketingMessageDto.setProductContent(ObjectUtils.isEmpty(requestMarketingMessageDto.getProductContent())?null:requestMarketingMessageDto.getProductContent());
        // 상품 이미지 파일 번호 셋팅
        marketingMessageDto.setCarouselFileNo(ObjectUtils.isArray(requestMarketingMessageDto.getCarouselFileNo())?null:requestMarketingMessageDto.getCarouselFileNo());
        return marketingMessageDto;
    }

    // 10자리의 UUID 생성
    public String makeShortUUID() {
        UUID uuid = UUID.randomUUID();
        return parseToShortUUID(uuid.toString());
    }

    // 파라미터로 받은 값을 10자리의 UUID로 변환
    private String parseToShortUUID(String uuid) {
        int l = ByteBuffer.wrap(uuid.getBytes()).getInt();
        return Integer.toString(l, 10);
    }

    // pageNation make 기능
    public Pagination makePagenation(Page<?> pageInfo){
        Pagination pagination = new Pagination();
        pagination.setPage(pageInfo.getNumber()+1);
        pagination.setSize(pageInfo.getSize());
        pagination.setTotal(pageInfo.getTotalElements());
        return pagination;
    }

    private Boolean dateValidation(CampaignDto campaignDto){
        String [] sendDateTimeList = campaignDto.getSendDateTime().split("\\:");

        // 공통적으로 한번만, 반복주기 모두 발송 시작일, 발송 시간은 체크 하기에 반복 타입에 따른 추가 검증만 하면 됨.
        if(campaignDto.getSendStartDate().isBefore(LocalDate.now().plusDays(1))){
            return false;
        }

        // 반복 주기 타입에 따른 분기 처리
        if(campaignDto.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_02.getCode())){
            // 12월2일 기준 비교 일을 +1일 하여  12월3일 기준으로 이전 날짜이면 현재 또는 과거 날짜 이므로 false처리
            if(campaignDto.getSendEndDate().isBefore(LocalDate.now().plusDays(1))){
                return false;
            }
        }
        return true;
    }

    // 추후 메시지 발송 프로그램에서 재사용할 수 있음.
    private Boolean sendDateTimeValidation(String [] sendDateTimeList){
        LocalDateTime currentDateTime = LocalDateTime.now();
        currentDateTime.getHour();
        Boolean result = true;

        // 발송 시간 체크 로직
        if(sendDateTimeList.length>0){
            // 현재 기준으로 11:11이면 11시 이상이여야 되고
            if(currentDateTime.getHour()<=Long.parseLong(sendDateTimeList[0])) {
                // 해당 값이 이상이면 분은 11분과 같으면 안되기에 현재값 이하면 안된다.(항상 미래의 값이여야 됨.)
                if(currentDateTime.getMinute()>=Long.parseLong(sendDateTimeList[1])) return false;
            }else return false;
        }else return false;

        return result;
    }

}
