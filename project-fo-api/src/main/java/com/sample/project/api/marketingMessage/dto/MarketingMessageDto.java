package com.sample.project.api.marketingMessage.dto;

import com.sample.project.api.marketingMessage.entity.AttachFileEntity;
import com.sample.project.api.marketingMessage.entity.MarketingMessageEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import com.sample.project.common.jpa.model.AdminBaseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;

/**
 * @author   	: user
 * @since    	: 2022/11/21
 * @desc     	: 마케팅 메시지 dto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingMessageDto extends AdminBaseDto implements ChangeableToFromEntity<MarketingMessageEntity> {

    @Schema(description = "메시지 식별번호")
    private Long messageNo;

    @Schema(description = "봇 ID", required = true, example = "637c56ce8f7dc436c344fcbparker")
    private String botId;

    @Schema(description = "케러셀 이미지 번호",  example = "2")
    private Long carouselFileNo;
    @Schema(description = "카드타입 구분 코드{ CARD_01:리스트 형, CARD_02:커머스 형 }", required = true,  example = "CARD_02")
    private String cardTypeCode;

    @Schema(description = "카드세부 타입 구분 코드{CARD_DTI_01: 신상품, CARD_DTI_02:구매 많은 상품, CARD_DTI_03: 리뷰 많은 상품, CARD_DTI_04:프로필}", required = true,  example = "CARD_DTI_01")
    private String cardDetailTypeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "케러셀 이미지 url",  example = "https://t1-sandbox.applecdn.net/dami/apple24/jrockmall/9dc016946cab486c88c5ab22c6b5619fnhgT.png?original")
    private String carouselFileUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "케러셀 이미지 url",  example = "university-campus.png")
    private String carouselFileName;

    @Schema(description = "상품 제목",  example = "상품 컬렉션")
    private String productTitle;

    @Schema(description = "신상품 설명",  example = "상품을 살펴보세요.")
    private String productContent;

    public MarketingMessageDto (MarketingMessageEntity marketingMessageEntity){
        from(marketingMessageEntity);
    }

    @Override
    public MarketingMessageEntity to() {
        return MarketingMessageEntity.builder()
                .messageNo(messageNo)
                .botId(botId)
                .carouselFile(!ObjectUtils.isEmpty(carouselFileNo) ? AttachFileEntity.builder().attachFileNo(carouselFileNo).build() : null)
                .cardTypeCode(cardTypeCode)
                .cardDetailTypeCode(cardDetailTypeCode)
                .productTitle(productTitle)
                .productContent(productContent)
                .regId(super.getRegId())
                .regDate(super.getRegDate())
                .updId(super.getUpdId())
                .updDate(super.getUpdDate())
                .build();
    }

    @Override
    public void from(MarketingMessageEntity entity) {
        this.messageNo = entity.getMessageNo();
        this.botId = entity.getBotId();

        if(!ObjectUtils.isEmpty(entity.getCarouselFile())){
            carouselFileNo = entity.getCarouselFile().getAttachFileNo();
            carouselFileUrl = entity.getCarouselFile().getFileUrl();
            carouselFileName = entity.getCarouselFile().getAttachFileName();
        }

        this.cardTypeCode = entity.getCardTypeCode();
        this.cardDetailTypeCode = entity.getCardDetailTypeCode();
        this.productTitle = entity.getProductTitle();
        this.productContent = entity.getProductContent();
        super.setRegId(entity.getRegId());
        super.setRegDate(entity.getRegDate());
        super.setUpdId(entity.getUpdId());
        super.setUpdDate(entity.getUpdDate());
    }
}
