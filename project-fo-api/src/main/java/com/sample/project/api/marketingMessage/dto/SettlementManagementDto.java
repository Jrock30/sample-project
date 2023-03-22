package com.sample.project.api.marketingMessage.dto;

import com.sample.project.api.marketingMessage.entity.SettlementManagementEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author   	: user
 * @since    	: 2022/12/28
 * @desc     	: 캠페인 dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementManagementDto implements ChangeableToFromEntity<SettlementManagementEntity> {
    @Schema(description = "시퀀스")
    private Long settlementManagementNo;
    @Schema(description = "쇼핑몰 ID")
    private String mallId;
    @Schema(description = "쇼핑몰 이름")
    private String mallName;
    @Schema(description = "정산명")
    private String settlementName;
    @Schema(description = "시작일")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @Schema(description = "종료일")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @Schema(description = "총 메시지 발송 수수료 금액")
    private Long totalSendVatAmount;
    @Schema(description = "총 발송 건수")
    private Long totalSendCnt;
    @Schema(description = "총 판매 수수료 금액")
    private BigDecimal totalSellVatAmount;
    @Schema(description = "총 판매 건수")
    private Long totalSellCnt;
    @Schema(description = "총 판매 금액")
    private Long totalSellAmount;
    @Schema(description = "가감금액")
    private Long addSubtractionAmount;
    @Schema(description = "총 정산 금액")
    private Long totalSettlementAmount;
    @Schema(description = "결제 상태")
    private String paymentStatus;

    public SettlementManagementDto(SettlementManagementEntity settlementManagementEntity) {
        from(settlementManagementEntity);
    }


    @Override
    public SettlementManagementEntity to() {
        return SettlementManagementEntity.builder()
                .settlementManagementNo(settlementManagementNo)
                .mallId(mallId)
                .mallName(mallName)
                .startDate(startDate)
                .endDate(endDate)
                .settlementName(settlementName)
                .totalSendVatAmount(totalSendVatAmount)
                .totalSendCnt(totalSendCnt)
                .totalSellVatAmount(totalSellVatAmount)
                .totalSellCnt(totalSellCnt)
                .totalSellAmount(totalSellAmount)
                .addSubtractionAmount(addSubtractionAmount)
                .totalSettlementAmount(totalSettlementAmount)
                .paymentStatus(paymentStatus)
                .build();
    }

    @Override
    public void from(SettlementManagementEntity entity) {
        this.settlementManagementNo = entity.getSettlementManagementNo();
        this.mallId = entity.getMallId();
        this.mallName = entity.getMallName();
        this.settlementName = entity.getSettlementName();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.totalSendVatAmount = entity.getTotalSendVatAmount();
        this.totalSendCnt = entity.getTotalSendCnt();
        this.totalSellVatAmount = entity.getTotalSellVatAmount();
        this.totalSellCnt = entity.getTotalSellCnt();
        this.totalSellAmount = entity.getTotalSellAmount();
        this.addSubtractionAmount = entity.getAddSubtractionAmount();
        this.totalSettlementAmount = entity.getTotalSettlementAmount();
        this.paymentStatus = entity.getPaymentStatus();
    }
}
