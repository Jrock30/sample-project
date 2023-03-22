package com.sample.project.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
public class CustomUserDetails implements UserDetails {

    @Schema(description = "회원 ID")
    private String userId;

    @Schema(description = "쇼핑몰 ID")
    private String mallId;

    @Schema(description = "쇼핑몰 이름")
    private String mallName;

    @Schema(description = "Apple24 ID")
    private String agencyId;

    @Schema(description = "Apple24 회원명")
    private String agencyUserName;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "핸드폰 번호")
    private String mobile;

    @Schema(description = "약관구분코드")
    private int termsTypeCode;

    @Schema(description = "메일인증여부")
    private int mailAuthYn;

    @Schema(description = "메일인증횟수")
    private int mailAuthCount;

    @Schema(description = "로그인실패횟수")
    private int loginFailCount;

    @Schema(description = "회원상태코드 {UNCERTIFIED: 메일 미인증 상태, NORMAL: 회원정상상태, SUSPENSION: 회원정지상태, WITHDRAWAL: 회원탈퇴상태}")
    private String memberStatusCode;

    @Schema(description = "가입일자")
    private LocalDateTime joinDate;

    @Schema(description = "최근로그인일자")
    private LocalDateTime lastLoginDate;

    @Schema(description = "등록일자")
    private LocalDateTime regDate;

    @Schema(description = "수정일자")
    private LocalDateTime updDate;

    @Schema(description = "권한정보")
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
