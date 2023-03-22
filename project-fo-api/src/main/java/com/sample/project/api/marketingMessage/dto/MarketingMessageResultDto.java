package com.sample.project.api.marketingMessage.dto;

import com.sample.project.api.marketingMessage.entity.MarketingMessageResultEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author   	: user
 * @since    	: 2022/11/21
 * @desc     	: 마케팅 메시지 발송 결과 dto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingMessageResultDto implements ChangeableToFromEntity<MarketingMessageResultEntity> {

    @Schema(description = "메시지 발송 결과 번호")
    private Long messageResultNo;

    @Schema(description = "봇 ID")
    private String botId;

    @Schema(description = "캠페인 식별번호")
    private Long campaignNo;

    @Schema(description = "캠페인 명")
    private String campaignName;

    @Schema(description = "메시지 발송 아이디")
    private Long messageSendId;

    @Schema(description = "발송 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate sendDate;

    @Schema(description = "발송 일차")
    private String sendDayCount;

    @Schema(description = "정산 월")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "Asia/Seoul")
    private LocalDate calculateMonth;

    @Schema(description = "요청 건수")
    private Long requestCnt;

    @Schema(description = "성공 건수")
    private Long successCnt;

    @Schema(description = "실패 건수")
    private Long failCnt;

    @Schema(description = "비용")
    private Long cost;

    @Schema(description = "클릭 수")
    private Long clickCnt;

    @Schema(description = "클릭 률")
    private BigDecimal clickRate;

    @Schema(description = "구매 수")
    private Long buyCnt;

    @Schema(description = "구매 전환률")
    private BigDecimal buyConvRate;

    @Schema(description = "구매 액")
    private Long buyAmount;

    @Schema(description = "ROAS")
    private Long roas;

    @Schema(description = "CPA")
    private Long cpa;

    @Schema(description = "판매 수수료 율")
    private BigDecimal buyCommissionRate;

    @Schema(description = "판매 수수료 액")
    private Long buyCommissionAmount;

    public MarketingMessageResultDto (MarketingMessageResultEntity marketingMessageResultEntity){
        from(marketingMessageResultEntity);
    }

    @Override
    public MarketingMessageResultEntity to() {
        return MarketingMessageResultEntity.builder()
                .messageResultNo(messageResultNo)
                .botId(botId)
                .campaignNo(campaignNo)
                .campaignName(campaignName)
                .messageSendId(messageSendId)
                .sendDate(sendDate)
                .calculateMonth(calculateMonth)
                .requestCnt(requestCnt)
                .successCnt(successCnt)
                .failCnt(failCnt)
                .cost(cost)
                .clickCnt(clickCnt)
                .clickRate(clickRate)
                .buyCnt(buyCnt)
                .buyConvRate(buyConvRate)
                .buyAmount(buyAmount)
                .roas(roas)
                .cpa(cpa)
                .buyCommissionRate(buyCommissionRate)
                .buyCommissionAmount(buyCommissionAmount)
                .build();
    }

    @Override
    public void from(MarketingMessageResultEntity entity) {
        this.messageResultNo = entity.getMessageResultNo();
        this.botId = entity.getBotId();
        this.campaignNo = entity.getCampaignNo();
        this.campaignName = entity.getCampaignName();
        this.messageSendId = entity.getMessageSendId();
        this.sendDate = entity.getSendDate();
        this.calculateMonth = entity.getCalculateMonth();
        this.requestCnt = entity.getRequestCnt();
        this.successCnt = entity.getSuccessCnt();
        this.failCnt = entity.getFailCnt();
        this.cost = entity.getCost();
        this.clickCnt = entity.getClickCnt();
        this.clickRate = entity.getClickRate();
        this.buyCnt = entity.getBuyCnt();
        this.buyConvRate = entity.getBuyConvRate();
        this.buyAmount = entity.getBuyAmount();
        this.roas = entity.getRoas();
        this.cpa = entity.getCpa();
        this.buyCommissionRate = entity.getBuyCommissionRate();
        this.buyCommissionAmount = entity.getBuyCommissionAmount();
    }
}
