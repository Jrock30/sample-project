package com.sample.project.api.login.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MemberBotDto {

    @Schema(description = "관여중인 봇(엑셀데이터 생성 시 필요)")
    private String botId;

    @Schema(description = "마케팅수신동의(엑셀데이터 생성 시 필요)")
    private String marketingYn;

    @QueryProjection
    public MemberBotDto(String botId
            , String marketingYn) {
        this.botId = botId;
        this.marketingYn = marketingYn;
    }
}
