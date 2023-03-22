package com.sample.project.api.marketingMessage.dto;


import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import com.sample.project.common.jpa.model.AdminBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

/**
 * @author   	: user
 * @since    	: 2022/11/21
 * @desc     	: 캠페인 dto
 */

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignDto extends AdminBaseDto implements ChangeableToFromEntity<CampaignEntity> {

    @Schema(description = "캠페인 식별번호", example = "0")
    private Long campaignNo;

    @Schema(description = "봇 ID",  required = true, example = "637c56ce8f7dc436c344fcbparker")
    private String botId;

    @JsonIgnore
    @Schema(description = "메시지 식별번호", example = "1")
    private Long messageNo;

    @Schema(description = "카드타입 구분 코드{ CARD_01:리스트 형, CARD_02:커머스 형 }", required = true,  example = "CARD_02")
    private String cardTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "카드타입 구분 코드명{ CARD_01:리스트 형, CARD_02:커머스 형 }")
    private String cardTypeCodeName;

    @Schema(description = "캠페인 명", required = true, example = "캠페인 테스트")
    private String campaignName;

    @Schema(description = "타겟 설정 구분 코드{ TARGET_01:테스트 번호, TARGET_02:최근 구매 경험, TARGET_03:신규 가입 고객}", required = true, example = "TARGET_02")
    private String targetTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "타겟 설정 구분 코드명{ TARGET_01:테스트 번호, TARGET_02:최근 구매 경험, TARGET_03:신규 가입 고객}")
    private String targetTypeCodeName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "타겟 설정 기준 월", example = "3")
    private String targetBaseMonth;
    @Schema(description = "타겟 테스트 모바일", example = "010-0000-0000")
    private String targetTestMobile;
    @Schema(description = "내용 설정 구분 코드 {CONTENT_01:신상품, CONTENT_02:구매 많은 상품, CONTENT_03:리뷰 많은 상품}", required = true, example = "CONTENT_02")
    private String contentTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "내용 설정 구분 코드 명 {CONTENT_01:신상품, CONTENT_02:구매 많은 상품, CONTENT_03:리뷰 많은 상품}")
    private String contentTypeCodeName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "내용 설정 기준 월", example = "8")
    private String contentBaseMonth;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송주기 설정 구분 코드{SEND_CY_01:한번만, SEND_CY_02:반복 발송}", example = "SEND_CY_02")
    private String sendTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송주기 설정 구분 코드명{SEND_CY_01:한번만, SEND_CY_02:반복 발송}")
    private String sendTypeCodeName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송주기 시작일",  example = "2022-11-22")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate sendStartDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송주기 종료일(format :2022-11-23)",  example = "2022-11-23")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate sendEndDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송시간",  example = "11:05")
    private String sendDateTime;

    @Schema(description = "캠페인 진행 상태 코드{CAMP_ST_01:진행 예정, CAMP_ST_02:진행 중, CAMP_ST_03: 진행 중지, CAMP_ST_04:진행 완료}", required = true, example = "CAMP_ST_01")
    private String campaignProgessStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "캠페인 진행 상태 코드명{CAMP_ST_01:진행 예정, CAMP_ST_02:진행 중, CAMP_ST_03: 진행 중지, CAMP_ST_04:진행 완료}")
    private String campaignProgessStatusName;

    @JsonIgnore
    @Schema(description = "색상{BLUE:파랑, RED:빨강, YELLOW:노랑}", required = true, example = "YELLOW")
    private String color;

    @Schema(description = "발송반복주기일", example = "1")
    private String sendIterationDay;

    @Schema(description = "발송반복주기 타입 코드", example = "DAY,WEEK")
    private String sendIterationTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송반복주기 타입 코드 명", example = "DAY(일),WEEK(주)")
    private String sendIterationTypeCodeName;

    @JsonIgnore
    @Schema(description = "삭제 여부", example = "Y")
    private String delYn;

    @JsonIgnore
    @Schema(description = "발송반복주기요일")
    private String sendIterationDayWeek;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "발송반복주기요일 리스트", example ="['MON','TUE','WED', 'THU', 'FRI', 'SAT', 'SUN']")
    private List<String> sendIterationDayWeekList; // front용 object

    @Schema(description = "마케팅 캠페인 색상", example = "{colorCode:#FFEAD1, colorName:주황색}")
    private BackGroundColor backGroundColor; // front용 object

    public CampaignDto(CampaignEntity campaignEntity){
        from(campaignEntity);
    }

    @Override
    public CampaignEntity to() {
        return CampaignEntity.builder()
                .campaignNo(campaignNo)
                .botId(botId)
                .messageNo(messageNo)
                .cardTypeCode(cardTypeCode)
                .campaignName(campaignName)
                .targetTypeCode(targetTypeCode)
                .targetBaseMonth(targetBaseMonth)
                .targetTestMobile(targetTestMobile)
                .contentTypeCode(contentTypeCode)
                .contentBaseMonth(contentBaseMonth)
                .sendTypeCode(sendTypeCode)
                .sendStartDate(sendStartDate)
                .sendEndDate(sendEndDate)
                .sendDateTime(sendDateTime)
                .campaignProgessStatus(campaignProgessStatus)
                .color(color)
                .sendIterationDay(sendIterationDay)
                .sendIterationTypeCode(sendIterationTypeCode)
                .sendIterationDayWeek(sendIterationDayWeek)
                .delYn(delYn)
                .regId(super.getRegId())
                .regDate(super.getRegDate())
                .updId(super.getUpdId())
                .updDate(super.getUpdDate())
                .build();
    }

    @Override
    public void from(CampaignEntity entity) {
        this.campaignNo = entity.getCampaignNo();
        this.botId = entity.getBotId();
        this.messageNo = entity.getMessageNo();
        this.cardTypeCode = entity.getCardTypeCode();
        this.campaignName = entity.getCampaignName();
        this.targetTypeCode = entity.getTargetTypeCode();
        this.targetBaseMonth = entity.getTargetBaseMonth();
        this.targetTestMobile = entity.getTargetTestMobile();
        this.contentTypeCode = entity.getContentTypeCode();
        this.contentBaseMonth = entity.getContentBaseMonth();
        this.sendTypeCode = entity.getSendTypeCode();
        this.sendStartDate = entity.getSendStartDate();
        this.sendEndDate = entity.getSendEndDate();
        this.sendDateTime = entity.getSendDateTime();
        this.campaignProgessStatus = entity.getCampaignProgessStatus();
        this.color = entity.getColor();
        this.sendIterationDay = entity.getSendIterationDay();
        this.sendIterationTypeCode =entity.getSendIterationTypeCode();
        this.sendIterationDayWeek = entity.getSendIterationDayWeek();
        this.delYn = entity.getDelYn();
        super.setRegId(entity.getRegId());
        super.setRegDate(entity.getRegDate());
        super.setUpdId(entity.getUpdId());
        super.setUpdDate(entity.getUpdDate());
    }
}
