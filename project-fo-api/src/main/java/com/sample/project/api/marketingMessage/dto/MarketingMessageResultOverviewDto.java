package com.sample.project.api.marketingMessage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketingMessageResultOverviewDto {

    @Schema(description = "총 건수",example = "100")
    private Long totalCnt;

    @Schema(description = "발송 성공 건수",example = "320")
    private Long successCnt;

    @Schema(description = "발송 비용 합계",example = "300000")
    private Long sumCost;

    @Schema(description = "구매 수량 합계",example = "300")
    private Long sumBuyCnt;

    @Schema(description = "구매 액 합계",example = "300000")
    private Long sumBuyAmount;

    @Schema(description = "판매 수수료 합계",example = "300000")
    private Long sumBuyCommissionAmount;

}
