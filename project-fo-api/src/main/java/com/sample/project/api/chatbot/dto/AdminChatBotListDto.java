package com.sample.project.api.apple.dto;

import com.sample.project.api.apple.enums.BotRole;
import com.sample.project.api.apple.enums.DelegationStatus;
import com.sample.project.common.utils.AesUtils;
import com.sample.project.common.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "사용자 봇 목록")
public class AdminAppleListDto {

    @Schema(description = "위임봇 ID (봇 ID)")
    private String delegatorBotId;

    @Schema(description = "봇 마스터 회원 ID")
    private String botMasterUserId;

    @Schema(description = "연락처")
    private String mobile;

    @Schema(description = "봇이름")
    private String botName;

    @Schema(description = "쇼핑몰명")
    private String mallId;

    @Schema(description = "Apple24 ID")
    private String agencyId;

    @Schema(description = "카카오 채널 검색용 ID")
    private String appleSearchId;

    @Schema(description = "봇 권한 { MASTER: 대표운영자, MANAGER: 운영자 }")
    private String permissionGroupId;

    @Schema(description = "봇 권한명")
    private String permissionGroupName;

    @Schema(description = "봇 위임-대행 상태 - \n " +
            "BEFORE: 위임-대행 전 / \n " +
            "REQUEST: 위임-대행 요청 중 / \n " +
            "UNDER: 위임-대행 중 / \n " +
            "FAIl: 위임-대행 요청 실패 / \n " +
            "STOP: 위임-대행 중지")
    private String delegateAgencyStatus;

    @Schema(description = "봇 위임-대행 상태명")
    private String delegateAgencyStatusName;

    @Schema(description = "어드민 키")
    private String adminKey;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "생성일")
    private LocalDateTime regDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "위임-대행 요청 일")
    private LocalDateTime delegateRequestDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "위임 대행 연결 일")
    private LocalDateTime delegateUnderDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "위임 대행 중지 일")
    private LocalDateTime delegateStopDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "위임 대행 실패 일")
    private LocalDateTime delegateFailDate;

    public AdminAppleListDto(
            String delegatorBotId
            , String botMasterUserId
            , String mobile
            , String botName
            , String mallId
            , String agencyId
            , String appleSearchId
            , String permissionGroupId
            , String delegateAgencyStatus
            , String adminKey
            , LocalDateTime regDate
            , LocalDateTime delegateRequestDate
            , LocalDateTime delegateUnderDate
            , LocalDateTime delegateStopDate
            , LocalDateTime delegateFailDate
    ) {
        this.delegatorBotId = delegatorBotId;
        this.botMasterUserId = botMasterUserId;
        try {
            this.mobile = CommonUtils.maskingPhoneNumber(AesUtils.decrypt(mobile));
        } catch (Exception e) {
            this.mobile = CommonUtils.maskingPhoneNumber(mobile);
        }
        this.botName = botName;
        this.mallId = mallId;
        this.agencyId = agencyId;
        this.appleSearchId = appleSearchId;
        this.permissionGroupId = permissionGroupId;
        this.permissionGroupName = BotRole.valueOf("BOT_"+permissionGroupId).codeName();;
        this.delegateAgencyStatus = delegateAgencyStatus;
        this.delegateAgencyStatusName = DelegationStatus.valueOf("DELEGATION_"+delegateAgencyStatus).codeName();;
        this.adminKey = adminKey;
        this.regDate = regDate;
        this.delegateRequestDate = delegateRequestDate;
        this.delegateUnderDate = delegateUnderDate;
        this.delegateStopDate = delegateStopDate;
        this.delegateFailDate = delegateFailDate;
    }
}
