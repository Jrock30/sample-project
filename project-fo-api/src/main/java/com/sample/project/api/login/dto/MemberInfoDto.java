package com.sample.project.api.login.dto;

import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.common.utils.AesUtils;
import com.sample.project.common.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemberInfoDto {

    @Schema(description = "회원 ID")
    private String userId;

    @Schema(description = "회원상태 코드명")
    private String memberStatusCodeName;

    @Schema(description = "핸드폰 번호")
    private String mobile;

    @Schema(description = "가입일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime joinDate;

    @Schema(description = "마스터 봇 이름")
    private String botName;

    @Schema(description = "Apple24 ID")
    @JsonProperty("apple24UserId")
    private String agencyId;

    @Schema(description = "마케팅 수신 동의 여부")
    private int termsOptional1Yn;

    @Schema(description = "관여중인 봇 목록")
    private List<String> botNameList;

    @QueryProjection
    public MemberInfoDto(String userId, String memberStatusCode, String mobile, LocalDateTime joinDate, String botName, String agencyId, int termsOptional1Yn) {
        this.userId = userId;
        this.memberStatusCodeName = MemberStateType.valueOf("MEMBER_STATE_" + memberStatusCode).codeName();
        try {
            this.mobile = CommonUtils.maskingPhoneNumber(AesUtils.decrypt(mobile));
        } catch (Exception e) {
            this.mobile = CommonUtils.maskingPhoneNumber(mobile);
        }
        this.joinDate = joinDate;
        this.botName = botName;
        this.agencyId = agencyId;
        this.termsOptional1Yn = termsOptional1Yn;
    }

}
