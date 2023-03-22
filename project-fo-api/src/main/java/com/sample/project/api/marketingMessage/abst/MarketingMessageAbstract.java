package com.sample.project.api.marketingMessage.abst;

import com.sample.project.api.marketingMessage.dto.*;
import com.sample.project.api.marketingMessage.dto.reponse.CampaignListResponseDto;
import com.sample.project.api.marketingMessage.dto.reponse.MarketingMessageResultListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.*;
import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.api.marketingMessage.entity.MarketingMessageEntity;
import com.sample.project.api.marketingMessage.enums.*;
import com.sample.project.api.marketingMessage.repository.*;
import com.sample.project.api.marketingMessage.util.MarketingUtil;
import com.sample.project.common.exception.Apple24AccessTokenExpiresException;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.wrapper.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_4010;
import static com.sample.project.common.type.ResponseErrorCode.FAIL_500;

/**
 * @param : CampaignDto
 * @author : user
 * @desc : 마케팅 메시지의 전반 적인 기능 모듈, 추후 다른 고객사들에 대한 확정성 고려
 * @since : 2022/11/23
 */

@Slf4j
@Transactional
@RequiredArgsConstructor
@Component
public abstract class MarketingMessageAbstract {

    private final CampaignRepository campaignRepository;
    private final HolidayRepository holidayRepository;
    private final MarketingUtil marketingUtil;
    private final MarketingMessageRepository marketingMessageRepository;
    private final AttachFileRepository attachFileRepository;
    private final MarketingMessageResultRepository marketingMessageResultRepository;
    private final WebClient batchWebClient;
    private final SettlementManagementRepository settlementManagementRepository;


    /*campaign Add Dto Validator*/
    public boolean campaignAddDtoValidator(CampaignDto campaignDto){
        return marketingUtil.campaignAddDtoValidator(campaignDto);
    }

    /*campaign Info save*/
    public Long saveCampaignInfo(CampaignDto campaignDto){
        Optional<CampaignEntity> optionalCampaignEntity  = campaignRepository.findById(campaignDto.getCampaignNo());

        if(!ObjectUtils.isEmpty(campaignDto.getSendTypeCode()) && campaignDto.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_01.getCode())) {
            campaignDto.setSendEndDate(campaignDto.getSendStartDate());
        }

        // 기존 데이터 등록되어 있는 데이터 유무에 따른 데이터 setting 분기 처리
        if(optionalCampaignEntity.isEmpty()){
            // 타겟 구분값이 테스트 타겟일 경우 발송 완료 처리
            if(campaignDto.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())){
                campaignDto.setCampaignProgessStatus(CampaignProgressStatus.CAMP_ST_04.getCode());// 처음 생성 시 상태 값은 진행 예정으로 고정
            }else{
                campaignDto.setCampaignProgessStatus(CampaignProgressStatus.CAMP_ST_01.getCode());// 처음 생성 시 상태 값은 진행 예정으로 고정
            }
        }else{
            campaignDto.setCampaignProgessStatus(optionalCampaignEntity.get().getCampaignProgessStatus());
            campaignDto.setRegDate(optionalCampaignEntity.get().getRegDate());
            campaignDto.setRegId(optionalCampaignEntity.get().getRegId());
        }

        // 타겟 구분값이 테스트 타겟일 경우 발송 시작, 종료일 현시점으로 등록
        if(campaignDto.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())){
            campaignDto.setSendStartDate(LocalDate.now());
            campaignDto.setSendEndDate(LocalDate.now());
            String stringBuilder = LocalDateTime.now().getHour() +":" +LocalDateTime.now().getMinute();
            campaignDto.setSendDateTime(stringBuilder);
        }

        // 마케팅 메시지 번호 매핑 로직
        MarketingMessageDto marketingMessageDto = new MarketingMessageDto(
                marketingMessageRepository.findAll().stream()
                        .filter(item -> item.getBotId().equals(campaignDto.getBotId()))
                        .filter(item -> item.getCardTypeCode().equals(campaignDto.getCardTypeCode()))
                        .findFirst()
                        .orElseThrow(() -> new NullPointerException("not found marketingMessageEntity Info : "+campaignDto.getBotId()))
        );
        campaignDto.setMessageNo(marketingMessageDto.to().getMessageNo());
        campaignDto.setDelYn("N");
        CampaignEntity returnEtntity = campaignRepository.saveAndFlush(campaignDto.to());
        log.debug("responseAgencyTokenDto >>> {}", campaignDto);
        return returnEtntity.getCampaignNo();
    }

    /*campaign schedule list search*/
    public List<CampaignDto> getCampaignScheduleList(RequestCampaignScheduleDto requestCampaignScheduleDto) {
        List<CampaignDto> campaignDtoList = campaignRepository.findAllByBotIdOrderByCampaignNoDesc(requestCampaignScheduleDto.getBotId()).stream().map(CampaignDto::new).collect(Collectors.toList());

        campaignDtoList = campaignDtoList.stream()
                .filter(item -> item.getCampaignProgessStatus().equals(CampaignProgressStatus.CAMP_ST_01.getCode()) || item.getCampaignProgessStatus().equals(CampaignProgressStatus.CAMP_ST_02.getCode()))
                .collect(Collectors.toList());

        campaignDtoList.forEach(item -> {
                    // 테스트 번호가 아니고, 한번만이 아닐 경우(반복 주기 설정이), 반복 주기가 주간인 경우
                    if(!item.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())&&!item.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_01.getCode()) && item.getSendIterationTypeCode().equals((SendIterrationType.WEEK.code()))){
                        // senditerationDayWeek data 변환  MON||TUE||
                        item.setSendIterationDayWeekList(marketingUtil.sendIterationDayWeekToListConvertor(item.getSendIterationDayWeek()));
                    }
                    item = marketingUtil.getCampaignCodeName(item);
        });
        return campaignDtoList;
    }

    /*campaign list search*/
    public Page<CampaignListResponseDto> searchCampaignList(SearchCampaignListDto searchCampaignListDto, Pageable pageable){
        return campaignRepository.searchCampaignList(searchCampaignListDto, pageable);
    }

    /*반복요일 list to String method*/
    public String sendIterationDayWeekListToString(List<String> sendIterationDayWeekList){
        return marketingUtil.stringListToStringConvertor(sendIterationDayWeekList);
    }

    /*공휴일 등록 메소드*/
    public void saveHolidayInfo(HolidayDto holidayDto){
        holidayRepository.save(holidayDto.to());
    }

    /*공휴일 조회 메소드*/
    public List<HolidayDto> getHolidayList(){
        return holidayRepository.findAllByOrderByDateAsc().stream().map(HolidayDto::new).collect(Collectors.toList());
    }

    /*캠페인 상세 조회*/
    public CampaignDto getCampaignDeatilInfo(Long campaignNo){
        Optional<CampaignEntity>optionalCampaignEntity = campaignRepository.findById(campaignNo);
        CampaignDto campaignDto = new CampaignDto(optionalCampaignEntity.orElseThrow(() -> new CustomException(FAIL_4010.message(), HttpStatus.INTERNAL_SERVER_ERROR)));

        campaignDto = marketingUtil.getCampaignCodeName(campaignDto);

        if(!ObjectUtils.isEmpty(campaignDto.getSendTypeCode()) && campaignDto.getSendTypeCode().equals(CampaignSendCycle.SEND_CY_02.getCode())&&
                campaignDto.getSendIterationTypeCode().equals(SendIterrationType.WEEK.getCode())){
            List<String> sendIterationDayWeekList = marketingUtil.sendIterationDayWeekToListConvertor(campaignDto.getSendIterationDayWeek());
            campaignDto.setSendIterationDayWeekList(sendIterationDayWeekList);
        }

        return campaignDto;
    }

    /*마케팅 메시지 > 캠페인 일정의 목록 진행 상태 처리 method*/
    public void campaignProgressStatusProcess(Long campaignNo, RequestCampaignListUpdateDto requestCampaignListUpdateDto){
        CampaignDto campaignDto = this.getCampaignDto(campaignNo);
        campaignDto.setCampaignProgessStatus(CampaignProgressStatus.valueOf(requestCampaignListUpdateDto.getCampaignProgessStatus()).getCode());
        campaignRepository.save(campaignDto.to());
    }

    /*마케팅 메시지 > 캠페인 일정의 목록 삭제 처리 method*/
    public void campaignDeleteProcess(Long campaignNo){
        CampaignDto campaignDto = this.getCampaignDto(campaignNo);
        campaignDto.setDelYn("Y");
        campaignRepository.save(campaignDto.to());
    }

    /*마케팅 메시지> 메시지 저장 */
    public MarketingMessageDto campaignMessageSettingSave(RequestMarketingMessageDto requestMarketingMessageDto, MultipartFile carouselFile){
        AttachFileDto attachFileDto;
        MarketingMessageDto marketingMessageDto = new MarketingMessageDto(marketingMessageRepository.findById(requestMarketingMessageDto.getMessageNo()).orElseThrow(() -> new NullPointerException("해당 하는 데이터가 없습니다.")));

        // request
        marketingMessageDto = marketingUtil.setRequestCampaignMessageSettingData(requestMarketingMessageDto,marketingMessageDto);

        // 케러셀의 데이터 형테에 따른 데이터 유무에 따른 기본값 설정 분기 처리
        marketingMessageDto = marketingUtil.carouselDefaultValueSetting(marketingMessageDto);

        if(!ObjectUtils.isEmpty(carouselFile)&&!carouselFile.isEmpty() && !marketingMessageDto.getCardTypeCode().equals(CampaignCardType.CARD_01.getCode())){
            // 케러셀 형 파일 정보 set
            attachFileDto = marketingUtil.saveCarouselFile(carouselFile);
            marketingMessageDto.setCarouselFileNo(attachFileRepository.saveAndFlush(attachFileDto.to()).getAttachFileNo());
            attachFileDto.setAttachFileNo(marketingMessageDto.getCarouselFileNo());
            marketingMessageDto = new MarketingMessageDto(marketingMessageRepository.saveAndFlush(marketingMessageDto.to()));
            marketingMessageDto.setCarouselFileName(attachFileDto.getAttachFileName());
            marketingMessageDto.setCarouselFileUrl(attachFileDto.getFileUrl());
            marketingMessageDto.setCarouselFileNo(attachFileDto.getAttachFileNo());
        }else{
            marketingMessageDto = new MarketingMessageDto(marketingMessageRepository.saveAndFlush(marketingMessageDto.to()));
        }
        return marketingMessageDto;
    }

    /*마케팅 메시지> 테스트 타겟일 시 즉시 발송 */
    public void sendEventApi(CampaignDto campaignDto){
        if(campaignDto.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())){
            // batch server로 연동
            batchWebClient.post()
                    .uri(campaignDto.getCampaignNo().toString()+"?phoneNumber="+campaignDto.getTargetTestMobile())
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            clientResponse -> Mono.error(new Apple24AccessTokenExpiresException(ResponseErrorCode.FAIL_4001.message()))
                    )
                    .bodyToMono(String.class)
                    .onErrorMap(e -> {
                        log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                        log.debug(CommonUtils.getPrintStackTrace(e));
                        return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                    })
                    .block();
        }
    }

    /*마케팅 메시지> 메시지 초기 생성 */
    public void campaignMessageInitSettingSave(RequestMarketingMessageDto requestMarketingMessageDto){
        MarketingMessageDto marketingMessageDto = new MarketingMessageDto();
        marketingMessageDto.setBotId(requestMarketingMessageDto.getBotId());
        // card 초기 데이터 생성 리스트, 신상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_01.getCode(), CampaignCardDetailType.CARD_DTI_01.getCode());
        // card 초기 데이터 생성 리스트, 구매 많은 상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_01.getCode(), CampaignCardDetailType.CARD_DTI_02.getCode());
        // card 초기 데이터 생성 리스트, 리뷰 많은 상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_01.getCode(), CampaignCardDetailType.CARD_DTI_03.getCode());
        // card 초기 데이터 생성 리스트, 프로필
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_01.getCode(), CampaignCardDetailType.CARD_DTI_04.getCode());
        // card 초기 데이터 생성 케러셀, 신상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_02.getCode(), CampaignCardDetailType.CARD_DTI_01.getCode());
        // card 초기 데이터 생성 케러셀, 구매 많은 상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_02.getCode(), CampaignCardDetailType.CARD_DTI_02.getCode());
        // card 초기 데이터 생성 케러셀, 리뷰 많은 상품
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_02.getCode(), CampaignCardDetailType.CARD_DTI_03.getCode());
        // card 초기 데이터 생성 케러셀, 프로필
        this.initMarketingMessageSave(marketingMessageDto, CampaignCardType.CARD_02.getCode(), CampaignCardDetailType.CARD_DTI_04.getCode());
    }

    public MarketingMessageDto getCampaignMessageSettingDetailInfo(Long messageNo){
        Optional<MarketingMessageEntity> entity = marketingMessageRepository.findById(messageNo);
        MarketingMessageDto marketingMessageDto = new MarketingMessageDto();
        if(entity.isPresent()){

            marketingMessageDto = new MarketingMessageDto(entity.get());
            if(!ObjectUtils.isEmpty(entity.get().getCarouselFile())){
                AttachFileDto attachFileDto = new AttachFileDto(entity.get().getCarouselFile());
                marketingMessageDto.setCarouselFileNo(attachFileDto.getAttachFileNo());
                marketingMessageDto.setCarouselFileName(attachFileDto.getAttachFileName());
                marketingMessageDto.setCarouselFileUrl(attachFileDto.getFileUrl());
            }
        }
        return marketingMessageDto;
    }

    /**
     * @author : user
     * @desc : 정산관리 데이터 조회 기능
     * @since : 2022/12/29
     */
    public Page<SettlementManagementDto> searchSettlementManagementList(SearchSettlementDto searchSettlementDto, Pageable pageable){
        return settlementManagementRepository.searchSettlementManagementList(searchSettlementDto, pageable);
    }

    // 메시지 초기 데이터 저장 메소드
    private void initMarketingMessageSave(MarketingMessageDto marketingMessageDto, String cardType, String cardDetailType){
        marketingMessageDto.setCardTypeCode(cardType);
        marketingMessageDto.setCardDetailTypeCode(cardDetailType);
        marketingMessageDto.setProductTitle(null);
        marketingMessageDto.setProductContent(null);
        marketingMessageDto.setRegId("project");

        marketingMessageDto = marketingUtil.carouselDefaultValueSetting(marketingMessageDto);
        marketingMessageRepository.save(marketingMessageDto.to());
    }

    /*마케팅 메시지> 메시지 설정 목록 조회 */
    public List<MarketingMessageDto> getMarketingMessageList(String botId){
        List<MarketingMessageDto> marketingMessageDtoList = new ArrayList<>();

        marketingMessageRepository.findAll().stream()
                .filter(item -> item.getBotId().equals(botId))
                .forEach(item -> {
                    MarketingMessageDto marketingMessageDto = new MarketingMessageDto(item);
                    marketingMessageDtoList.add(marketingMessageDto);
                });

        return marketingMessageDtoList;
    }

    /*발송 결과 조회 */
    public MarketingMessageResultListResponseDto getMarketingMessageResultList(SearchMessageResultDto searchMessageResultDto, Pageable pageable){
        return this.messageResultReponseDataCreate(marketingMessageResultRepository.searchMarketingMessageResultList(searchMessageResultDto, pageable)
                , marketingMessageResultRepository.searchMarketingMessageResultListNoPaging(searchMessageResultDto));
    }
    /*발송 결과 조회(페이징 X) */
    public List<MarketingMessageResultDto> getMarketingMessageResultListNoPaging(SearchMessageResultDto searchMessageResultDto){
        return marketingMessageResultRepository.searchMarketingMessageResultListNoPaging(searchMessageResultDto);
    }

    // pageNation make 기능
    public Pagination makePagenation(Page<?> pageInfo){
        return marketingUtil.makePagenation(pageInfo);
    }

    // campaign번호로 DTO데이터 생성
    private CampaignDto getCampaignDto(Long campaignNo){
        CampaignEntity campaignEntity = campaignRepository.findById(campaignNo).orElseThrow(() -> new NullPointerException("not found campaignEntity Result"));
        return new CampaignDto(campaignEntity);
    }

    // 발송 결과 responseData 생성 기능
    private MarketingMessageResultListResponseDto messageResultReponseDataCreate(Page<MarketingMessageResultDto> marketingMessageResultDtoPage, List<MarketingMessageResultDto> marketingMessageResultDtoList){
        return MarketingMessageResultListResponseDto.builder()
                .marketingMessageResultDtoPage(marketingMessageResultDtoPage)
                .marketingMessageResultOverviewDto(this.messageResultOverViewDataCreate(marketingMessageResultDtoList))
                .build();
    }

    // 발송 결과 overview data 생성
    private MarketingMessageResultOverviewDto messageResultOverViewDataCreate(List<MarketingMessageResultDto> marketingMessageResultDtoPage){
        return MarketingMessageResultOverviewDto.builder()
                .totalCnt((long) marketingMessageResultDtoPage.size())
                .successCnt(marketingMessageResultDtoPage.stream()
                        .filter(item -> !ObjectUtils.isEmpty(item.getSuccessCnt()))
                        .filter(item -> item.getSuccessCnt() >0).mapToLong(MarketingMessageResultDto::getSuccessCnt).sum())
                .sumCost(marketingMessageResultDtoPage.stream()
                        .filter(item -> !ObjectUtils.isEmpty(item.getCost()))
                        .filter(item -> item.getCost() >0).mapToLong(MarketingMessageResultDto::getCost).sum())
                .sumBuyCnt(marketingMessageResultDtoPage.stream()
                        .filter(item -> !ObjectUtils.isEmpty(item.getBuyCnt()))
                        .filter(item -> item.getBuyCnt() >0).mapToLong(MarketingMessageResultDto::getBuyCnt).sum())
                .sumBuyAmount(marketingMessageResultDtoPage.stream()
                        .filter(item -> !ObjectUtils.isEmpty(item.getBuyAmount()))
                        .filter(item -> item.getBuyAmount() >0).mapToLong(MarketingMessageResultDto::getBuyAmount).sum())
                .sumBuyCommissionAmount(marketingMessageResultDtoPage.stream()
                        .filter(item -> !ObjectUtils.isEmpty(item.getBuyCommissionAmount()))
                        .filter(item -> item.getBuyCommissionAmount() >0).mapToLong(MarketingMessageResultDto::getBuyCommissionAmount).sum())
                .build();
    }
}
