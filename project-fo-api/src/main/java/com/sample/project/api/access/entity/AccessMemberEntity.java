package com.sample.project.api.access.entity;

import com.sample.project.api.login.entity.LoginMemberPermissionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "MEMBER")
@Schema(description = "회원 엔티티")
public class AccessMemberEntity implements Serializable  {

    @Id
    @Column(name = "USER_ID")
    @Schema(description = "회원 ID")
    private String userId;

    @Column(name = "MALL_ID")
    @Schema(description = "쇼핑몰 ID")
    private String mallId;

    @Column(name = "MALL_NAME")
    @Schema(description = "쇼핑몰 이름")
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

    @Column(name = "JOIN_DATE")
    @Schema(description = "가입일자")
    private LocalDateTime joinDate;

    @Column(name = "UPD_DATE")
    private LocalDateTime updDate;

    @Column(name = "MEMBER_STATUS_CODE")
    @Schema(description = "회원상태코드 {UNCERTIFIED: 메일 미인증 상태, NORMAL: 회원정상상태, SUSPENSION: 회원정지상태, WITHDRAWAL: 회원탈퇴상태}")
    private String memberStatusCode;

    @Schema(description = "회원권한")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "USER_ID", insertable = false, updatable = false, nullable = false)
    @JoinColumn(name = "USER_ID")
    private LoginMemberPermissionEntity loginMemberPermissionEntity;

    public void changeMemberState(String memberStatusCode) {
        this.memberStatusCode = memberStatusCode;
        this.joinDate = LocalDateTime.now();
    }

    public void changeAgencyInfo(String mallId, String mallName, String agencyUserName, String agencyId, String role) {
        this.mallId = mallId;
        this.mallName = mallName;
        this.agencyId = agencyId;
        this.updDate = LocalDateTime.now();
        this.agencyUserName = agencyUserName;
//        this.loginMemberPermissionEntity.getPermissionGroupId() = role;
    }
}
