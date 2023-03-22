package com.sample.project.api.login.dto;

import com.sample.project.api.login.entity.LoginMemberEntity;
import com.sample.project.api.login.entity.LoginMemberPermissionEntity;
import com.sample.project.security.type.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

@Schema(description = "로그인")
@Data
public class RequestSignupDto implements Serializable {

    @Schema(description = "유저명", required = true, example = "admin@damiadmin.com")
    @NotEmpty(message = "{member.empty.user.id}")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{3,}+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "{member.pattern.user.id.error}")
    private String userId;

    @Schema(description = "비밀번호", required = true, example = "damiadmin12#$")
    @NotEmpty(message = "{member.empty.password}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,32}$", message = "{member.pattern.password.error}")
    @Size(min = 8, max = 32)
    private String password;

    @Schema(description = "비밀번호 확인", required = true, example = "damiadmin12#$")
    @NotEmpty(message = "{member.empty.password.confirm}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,32}$", message = "{member.pattern.password.error}")
    @Size(min = 8, max = 32)
    private String passwordConfirm;

    @JsonIgnore
    @Schema(description = "회원상태코드 {UNCERTIFIED: 메일 미인증 상태, NORMAL: 회원정상상태, SUSPENSION: 회원정지상태, WITHDRAWAL: 회원탈퇴상태}")
    private String memberStatusCode;

    @Min(value = 1, message = "{member.empty.terms.required}")
    @Max(value = 1, message = "{member.empty.terms.required}")
    @Schema(description = "필수동의여부2, {0: 미동의, 1: 동의}")
    private int termsRequire1Yn = 0;

    @Min(value = 1, message = "{member.empty.terms.required}")
    @Max(value = 1, message = "{member.empty.terms.required}")
    @Schema(description = "필수동의여부2, {0: 미동의, 1: 동의}")
    private int termsRequire2Yn = 0;

    @Schema(description = "선택동의여부, {0: 미동의, 1: 동의}")
    private int termsOptional1Yn = 0;

    @Schema(description = "hmac")
    private String hmac;

    public LoginMemberEntity toEntity() {
        LoginMemberPermissionEntity roleUser = LoginMemberPermissionEntity.builder()
                .userId(userId)
                .permissionGroupId(RoleType.USER.code())
                .build();
        return LoginMemberEntity.builder()
                .userId(userId)
                .password(password)
                .termsRequire1Yn(termsRequire1Yn)
                .termsRequire2Yn(termsRequire2Yn)
                .termsOptional1Yn(termsOptional1Yn)
                .loginMemberPermissionEntity(roleUser)
                .build();
    }

}
