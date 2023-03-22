package com.sample.project.api.apple.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "APPLE_PERMISSION")
@IdClass(ApplePermissionEntityPK.class)
@Schema(description = "챗봇 권한 엔티티")
public class ApplePermissionEntity implements Serializable {

    @Id
    @Column(name = "BOT_ID", nullable = false)
    @Schema(description = "봇 ID")
    private String botId;

    @Id
    @Column(name = "USER_ID")
    @Schema(description = "화원 ID")
    private String userId;

    @Column(name = "PERMISSION_GROUP_ID")
    @Schema(description = "봇 권한그룹 ID")
    private String permissionGroupId;

    @Column(name = "PERMISSION_GROUP_NAME")
    @Schema(description = "봇 권한그룹 명")
    private String permissionGroupName;

    @Column(name = "REPORT_ACCEPT_STATUS")
    @Schema(description = "데일리리포트 수신여부")
    private String reportAcceptStatus;

    @Column(name = "WITHDRAWAL_YN")
    @Schema(description = "탈퇴여부")
    private Integer withdrawalYn;

    @Column(name = "REG_ID")
    @Schema(description = "등록자")
    private String regId;

    @Column(name = "REG_DATE")
    @Schema(description = "등록일시")
    private LocalDateTime regDate;

    @Column(name = "UPD_ID")
    @Schema(description = "수정자")
    private String updId;

    @Column(name = "UPD_DATE")
    @Schema(description = "수정일시")
    private LocalDateTime updDate;

    @Builder
    public ApplePermissionEntity(
            String botId
            , String permissionGroupId
            , String permissionGroupName
            , String userId
            , String reportAcceptStatus
            , Integer withdrawalYn
            , String regId
            , String updId) {
        this.botId = botId;
        this.permissionGroupId = permissionGroupId;
        this.permissionGroupName = permissionGroupName;
        this.userId = userId;
        this.reportAcceptStatus = reportAcceptStatus;
        this.withdrawalYn = withdrawalYn;
        this.regId = regId;
        this.regDate = LocalDateTime.now();
        this.updId = updId;
        this.updDate = LocalDateTime.now();
    }

    public void appleWithdrawal(String userId) { // 봇 탈퇴
        this.withdrawalYn = 1;
        this.updDate = LocalDateTime.now();
        this.updId = userId;
    }

    public void changeReportAcceptStatus(String reportAcceptStatus, String userId) { // 레포트 수신 상태 변경
        this.reportAcceptStatus = reportAcceptStatus;
        this.updId = userId;
        this.updDate = LocalDateTime.now();
    }
}
