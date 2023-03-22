package com.sample.project.api.login.entity;

import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.security.SecurityUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString(of = {"userId", "mobile", "password", "termsTypeCode", "mailAuthYn", "mailAuthCount", "loginFailCount", "memberStatusCode", "joinDate", "lastLoginDate", "regDate", "updDate", "termsRequire1Yn", "termsRequire2Yn", "termsOptional1Yn"})
@Table(name = "MEMBER")
@Schema(description = "회원 엔티티")
public class LoginMemberEntity implements Serializable  {

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
    private int termsTypeCode;

    @Column(name = "MAIL_AUTH_YN")
    @Schema(description = "메일인증여부")
    private int mailAuthYn;

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
    @Schema(description = "필수동의여부1")
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

    @Column(name = "LOGIN_FAIL_DATE")
    @Schema(description = "로그인실패일시")
    private LocalDateTime loginFailDate;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "UPD_DATE")
    private LocalDateTime updDate;

    @Column(name = "UPD_ID")
    private String updId;

    @Schema(description = "회원권한")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "USER_ID", insertable = false, updatable = false, nullable = false)
    @JoinColumn(name = "USER_ID")
    private LoginMemberPermissionEntity loginMemberPermissionEntity;

    @Builder
    public LoginMemberEntity(String userId, String password, int termsRequire1Yn, int termsRequire2Yn, int termsOptional1Yn, String memberStatusCode, LoginMemberPermissionEntity loginMemberPermissionEntity) {
        this.userId = userId;
        this.password = password;
        this.termsRequire1Yn = termsRequire1Yn;
        this.termsRequire2Yn = termsRequire2Yn;
        this.termsOptional1Yn = termsOptional1Yn;
        this.memberStatusCode = MemberStateType.MEMBER_STATE_UNCERTIFIED.code();
        this.joinDate = LocalDateTime.now();
        this.regDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
        this.loginMemberPermissionEntity = loginMemberPermissionEntity;
    }

    public void changeLastLogin() { // 최근로그인 일자 변경
        this.lastLoginDate = LocalDateTime.now();
    }

    public void changePassword(String password) { // 비밀번호 변경
        this.password = password;
        this.updDate = LocalDateTime.now();
//        changeUpdInfo();
    }

    public void changeMobile(String mobile) { // 핸드폰번호 변경
        this.mobile = mobile;
//        changeUpdInfo();
    }

    public void changeTermsOptional1Yn(int termsOptional1Yn) {
        this.termsOptional1Yn = termsOptional1Yn;
        changeUpdInfo();
    }

    public void addAuthMailCount() { // 메일 전송 카운트 ++
        this.mailAuthCount = this.getMailAuthCount() + 1;
        this.mailAuthDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }

    public void resetAuthMailCount() { // 메일 전송 카운트 리셋
        this.mailAuthCount = 0;
        this.mailAuthDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }

    public void withdrawal() { // 회원 탈퇴
        this.memberStatusCode = MemberStateType.MEMBER_STATE_WITHDRAWAL.code();
        this.withdrawalDate = LocalDateTime.now();
        changeUpdInfo();
    }

    public void changeMemberInfo(String password, int termsRequire1Yn, int termsRequire2Yn, int termsOptional1Yn) {
        this.password = password;
        this.memberStatusCode = MemberStateType.MEMBER_STATE_NORMAL.code();
        this.termsRequire1Yn = termsRequire1Yn;
        this.termsRequire2Yn = termsRequire2Yn;
        this.termsOptional1Yn = termsOptional1Yn;
        changeUpdInfo();
    }

    public void addLoginFailCount() { // 로그인 실패 횟수 증가
        this.loginFailCount++;
        this.loginFailDate = LocalDateTime.now();
    }

    public void resetLoginFailCount() { // 로그인 실패 횟수 리셋
        this.loginFailCount = 0;
        this.loginFailDate = null;
    }

    private void changeUpdInfo() { // 업데이트 정보 입력
        this.updId = SecurityUtils.getCurrentUserId().get();
        this.updDate = LocalDateTime.now();
    }
}
