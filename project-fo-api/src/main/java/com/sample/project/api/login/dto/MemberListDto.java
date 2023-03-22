package com.sample.project.api.login.dto;

import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.common.utils.AesUtils;
import com.sample.project.common.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberListDto {

    @Schema(description = "회원 ID")
    private String userId;

    @Schema(description = "회원상태코드 {UNCERTIFIED: 메일 미인증 상태, NORMAL: 회원정상상태, SUSPENSION: 회원정지상태, WITHDRAWAL: 회원탈퇴상태}")
    private String memberStatusCode;

    @Schema(description = "회원상태 코드명")
    private String memberStatusCodeName;

    @Schema(description = "핸드폰 번호")
    private String mobile;

    @Schema(description = "가입일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime joinDate;

    @Schema(description = "마스터 봇 아이디")
    private String botId;

    @Schema(description = "마스터 봇 이름")
    private String botName;

    @Schema(description = "Apple24 ID")
    @JsonProperty("apple24UserId")
    private String agencyId;

    @Column(name = "AGENCY_USER_NAME")
    @JsonProperty("apple24UserName")
    @Schema(description = "Apple24 회원명")
    private String agencyUserName;

    @Column(name = "MALL_ID")
    @Schema(description = "쇼핑몰 ID")
    private String mallId;

    @Column(name = "MALL_NAME")
    @Schema(description = "쇼핑몰 이름")
    private String mallName;

    @Column(name = "ADMIN_MEMO")
    @Schema(description = "관리자 메모")
    private String adminMemo;

    @Schema(description = "관여중인 봇 리스트(엑셀데이터 생성 시 필요)")
    private List<String> botIdList;

    @Schema(description = "마케팅수신동의(엑셀데이터 생성 시 필요)")
    private String marketingYn;


    @QueryProjection
    public MemberListDto(String userId
            , String memberStatusCode
            , String mobile
            , LocalDateTime joinDate
            , String botId
            , String botName
            , String agencyId
            , String agencyUserName
            , String mallId
            , String mallName
            , String adminMemo) {
        this.userId = userId;
        this.memberStatusCode = memberStatusCode;
        this.memberStatusCodeName = MemberStateType.valueOf("MEMBER_STATE_" + memberStatusCode).codeName();
        try {
            this.mobile = CommonUtils.maskingPhoneNumber(AesUtils.decrypt(mobile));
        } catch (Exception e) {
            this.mobile = CommonUtils.maskingPhoneNumber(mobile);
        };
        this.joinDate = joinDate;
        this.botId = botId;
        this.botName = botName;
        this.agencyId = agencyId;
        this.agencyUserName = agencyUserName;
        this.mallId = mallId;
        this.mallName = mallName;
        this.adminMemo = adminMemo;
    }
}
