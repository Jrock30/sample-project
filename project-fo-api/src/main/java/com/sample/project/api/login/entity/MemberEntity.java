package com.sample.project.api.login.entity;

import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.security.SecurityUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
@Entity
@ToString
@Table(name = "MEMBER")
@Schema(description = "회원 엔티티")
public class MemberEntity implements Serializable  {

    @Id
    @Column(name = "USER_ID")
    @Schema(description = "회원 ID")
    private String userId;

    @Column(name = "MALL_ID")
    @Schema(description = "쇼핑몰 ID")
    private String mallId;

    @Column(name = "MALL_NAME")
    @Schema(description = "쇼핑몰 ID")
    private String mallName;

    @Column(name = "AGENCY_ID")
    @Schema(description = "Apple24 ID")
    private String agencyId;

    @Column(name = "AGENCY_USER_NAME")
    @Schema(description = "Apple24 회원명")
    private String agencyUserName;

    @Column(name = "MOBILE")
    @Schema(description = "핸드폰 번호")
    private String mobile;

    @Column(name = "PASSWORD", nullable = false)
    @Schema(description = "비밀번호")
    private String password;

    @Column(name = "TERMS_TYPE_CODE")
    @Schema(description = "약관구분코드")
    private Integer termsTypeCode;

    @Column(name = "MAIL_AUTH_YN")
    @Schema(description = "메일인증여부")
    private Integer mailAuthYn;

    @Column(name = "MAIL_AUTH_COUNT")
    @Schema(description = "메일인증횟수")
    private int mailAuthCount;

    @Column(name = "MAIL_AUTH_DATE")
    @Schema(description = "메일인증 날짜")
    private LocalDateTime mailAuthDate;

    @Column(name = "LOGIN_FAIL_COUNT")
    @Schema(description = "로그인실패횟수")
    private int loginFailCount;

    @Column(name = "MEMBER_STATUS_CODE", nullable = false)
    @Schema(description = "회원상태코드 {UNCERTIFIED: 메일 미인증 상태, NORMAL: 회원정상상태, SUSPENSION: 회원정지상태, WITHDRAWAL: 회원탈퇴상태}")
    private String memberStatusCode;

    @Column(name = "TERMS_REQUIRE1_YN")
    @Schema(description = "필수동의여부1")
    private int termsRequire1Yn;

    @Column(name = "TERMS_REQUIRE2_YN")
    @Schema(description = "필수동의여부2")
    private int termsRequire2Yn;

    @Column(name = "TERMS_OPTIONAL1_YN")
    @Schema(description = "선택동의여부1")
    private int termsOptional1Yn;

    @Column(name = "JOIN_DATE")
    @Schema(description = "가입일자")
    private LocalDateTime joinDate;

    @Column(name = "INVITE_DATE")
    @Schema(description = "가입초대일시")
    private LocalDateTime inviteDate;

    @Column(name = "SUSPENSION_DATE")
    @Schema(description = "정지일시")
    private LocalDateTime suspensionDate;

    @Column(name = "WITHDRAWAL_DATE")
    @Schema(description = "탈퇴일시")
    private LocalDateTime withdrawalDate;

    @Column(name = "LAST_LOGIN_DATE")
    @Schema(description = "최근로그인일자")
    private LocalDateTime lastLoginDate;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "UPD_DATE")
    private LocalDateTime updDate;

    @Column(name = "UPD_ID")
    private String updId;

    @Column(name = "ADMIN_MEMO")
    @Schema(description = "관리자 메모")
    private String adminMemo;
    public void suspension() { // 활동 정지 상태 변환
        this.memberStatusCode = MemberStateType.MEMBER_STATE_SUSPENSION.code();
        this.suspensionDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
        this.updId = SecurityUtils.getCurrentUserId().get();
    }

    public void unSuspension() { // 활동 정지 상태 해제
        this.memberStatusCode = MemberStateType.MEMBER_STATE_NORMAL.code();
        this.suspensionDate = null;
        this.updDate = LocalDateTime.now();
        this.updId = SecurityUtils.getCurrentUserId().get();
    }

    public void uncertified() { // 메일 미인증 상태
        this.memberStatusCode = MemberStateType.MEMBER_STATE_UNCERTIFIED.code();
        this.suspensionDate = null;
        this.updDate = LocalDateTime.now();
        this.inviteDate = LocalDateTime.now();
    }

    public void mailInvite() {
        this.memberStatusCode = MemberStateType.MEMBER_STATE_NORMAL.code();
        this.suspensionDate = null;
        this.joinDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
        this.updId = SecurityUtils.getCurrentUserId().get();
    }
}
