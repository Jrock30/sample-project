package com.sample.project.api.apple.entity;

import com.sample.project.api.apple.dto.request.RequestAppleDelegateDto;
import com.sample.project.api.apple.enums.DelegationProgress;
import com.sample.project.security.SecurityUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.sample.project.api.apple.enums.DelegationStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "APPLE")
@Schema(description = "챗봇 엔티티")
public class AppleEntity implements Serializable {

    @Id
    @Column(name = "BOT_ID")
    @Schema(description = "봇 ID")
    private String botId;

    @Column(name = "BOT_NAME")
    @Schema(description = "봇이름")
    private String botName;

    @Column(name = "APPLE_SEARCH_ID")
    @Schema(description = "카카오채널 검색용 ID")
    private String appleSearchId;

    @Column(name = "AGENCY_ID")
    @Schema(description = "카페24 ID")
    private String agencyId;

    @Column(name = "MALL_ID")
    @Schema(description = "쇼핑몰 ID")
    private String mallId;

    @Column(name = "MALL_NAME")
    @Schema(description = "쇼핑몰 이름")
    private String mallName;

    @Column(name = "ADMIN_KEY")
    @Schema(description = "어드민 키")
    private String adminKey;

    @Column(name = "DELEGATE_AGENCY_STATUS")
    @Schema(description = "위임대행상태")
    private String delegateAgencyStatus;

    @Column(name = "DELEGATION_PROGRESS")
    @Schema(description = "위임-대행 진행상태")
    private String delegationProgress;

    @Column(name = "TALK_USE_YN")
    @Schema(description = "상담톡 사용여부")
    private Integer talkUseYn;

    @Column(name = "WELCOME_MESSAGE")
    @Schema(description = "웰컴메시지")
    private String welcomeMessage;

    @Column(name = "DELEGATE_AGENCY_FAIL_REASON")
    @Schema(description = "위임대행실패사유")
    private String delegateAgencyFailReason;

    @Column(name = "DELETE_YN")
    @Schema(description = "삭제여부")
    private Integer deleteYn;

    @Column(name = "DELEGATE_REQUEST_DATE")
    @Schema(description = "위임-대행 요청 일")
    private LocalDateTime delegateRequestDate;

    @Column(name = "DELEGATE_UNDER_DATE")
    @Schema(description = "위임 대행 연결 일")
    private LocalDateTime delegateUnderDate;

    @Column(name = "DELEGATE_STOP_DATE")
    @Schema(description = "위임 대행 중지 일")
    private LocalDateTime delegateStopDate;

    @Column(name = "DELEGATE_FAIL_DATE")
    @Schema(description = "위임 대행 실패 일")
    private LocalDateTime delegateFailDate;

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
    public AppleEntity(
            String botId
            , String botName
            , String appleSearchId
            , String agencyId
            , String mallId
            , String mallName
            , String delegateAgencyStatus
            , String delegationProgress
            , Integer talkUseYn
            , String welcomeMessage
            , String delegateAgencyFailReason
            , Integer deleteYn
            , String regId
            , String updId) {
        this.botId = botId;
        this.botName = botName;
        this.appleSearchId = appleSearchId;
        this.agencyId = agencyId;
        this.mallId = mallId;
        this.mallName = mallName;
        this.delegateAgencyStatus = delegateAgencyStatus;
        this.delegationProgress = delegationProgress;
        this.talkUseYn = talkUseYn;
        this.welcomeMessage = welcomeMessage;
        this.delegateAgencyFailReason = delegateAgencyFailReason;
        this.deleteYn = deleteYn;
        this.adminKey = null;
        this.delegateUnderDate = null;
        this.delegateStopDate = null;
        this.delegateFailDate = null;
        this.delegateRequestDate = null;
        this.regId = regId;
        this.regDate = LocalDateTime.now();
        this.updId = updId;
        this.updDate = LocalDateTime.now();
    }

    public void changeBotInfo(String botName, String welcomeMessage, Integer talkUseYn, String appleSearchId, String updId, LocalDateTime updDate) {
        this.botName = botName;
        this.welcomeMessage = welcomeMessage;
        this.talkUseYn = talkUseYn;
        this.appleSearchId = appleSearchId;
        this.updId = updId;
        this.updDate = updDate;
    }

    public void deleteApple(String userId) { // 챗봇 삭제
        this.deleteYn = 1;
        this.updDate = LocalDateTime.now();
        this.updId = userId;
    }

    public void delegateApple(RequestAppleDelegateDto requestAppleDelegateDto) { // 사용자 위임대행 요청
        this.delegateAgencyStatus = requestAppleDelegateDto.getDelegateAgencyStatus();
        this.delegationProgress = requestAppleDelegateDto.getDelegationProgress();
        this.updDate = LocalDateTime.now();
        this.updId = requestAppleDelegateDto.getUserId();
        this.updateDelegationProgressDate();
    }

    public void delegateApple(String delegateAgencyStatus) { // 위임대행 상태 변경
        this.delegateAgencyStatus = delegateAgencyStatus;
        this.updDate = LocalDateTime.now();
        this.updId = SecurityUtils.getCurrentUserId().get();
        updateDelegationProgressDate();
    }

    private void updateDelegationProgressDate() {
        if (this.delegateAgencyStatus.equals(DELEGATION_REQUEST.code())) { // 위임-대행 요청
            this.delegateRequestDate = LocalDateTime.now();
        } else if (this.delegateAgencyStatus.equals(DELEGATION_UNDER.code())) { // 위임-대행중(연결)
            this.delegateUnderDate = LocalDateTime.now();
        } else if (this.delegateAgencyStatus.equals(DELEGATION_FAIL_NOT_APP.code()) // 위임-대행 요청 실패(앱 관리자 미 초대
                || this.delegateAgencyStatus.equals(DELEGATION_FAIL_NOT_ADMIN.code()) // 위임-대행 요청 실패(채널관리자 미 초대)
                || this.delegateAgencyStatus.equals(DELEGATION_FAIL_SYSTEM.code()) // 위임-대행 요청 실패(시스템 오류)
        ) { // 위임-대행 실패
            this.delegateFailDate = LocalDateTime.now();
        } else if (this.delegateAgencyStatus.equals(DELEGATION_STOP_NOT_PAY.code())  // 위임-대행 중지(요청)
                || this.delegateAgencyStatus.equals(DELEGATION_STOP_REQUEST.code())) { // 위임-대행 중지(미결제)
            this.delegateStopDate = LocalDateTime.now();
        }
//        else if (this.delegateAgencyStatus.equals(DELEGATION_DELETE_ADMIN.code())) { // 관리자 삭제
//
//        }
    }

    public void updateAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public void updateDelegateAgencyFailReason(String delegateAgencyFailReason) {
        this.delegateAgencyFailReason = delegateAgencyFailReason;
    }

    /**
     * 관리자 봇 상태 리셋(개발 편의를 위한 API)
     */
    public void changeBotStauts() {
        this.delegateRequestDate = null;
        this.delegateUnderDate = null;
        this.delegateStopDate = null;
        this.delegateFailDate = null;
        this.delegateAgencyStatus = DELEGATION_BEFORE.code();
        this.delegationProgress = DelegationProgress.DP_FIRST.code();
    }

    public void updateAppleSearchId(String appleSearchId) { // 카카오 검색용 아이디 등록
        this.appleSearchId = appleSearchId;
    }
}

