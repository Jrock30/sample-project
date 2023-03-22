package com.sample.project.api.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "비밀번호 변경 DTO")
@Data
public class RequestChangePasswordDto {

//    @Schema(description = "유저명", required = true, example = "admin@damiadmin.com")
//    @NotEmpty(message = "{member.empty.user.id}")
//    @Pattern(regexp = "^[A-Za-z0-9._%+-]{3,}+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "{member.pattern.user.id.error}")
//    private String userId;

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

    @Schema(description = "토큰", required = true)
    @NotEmpty(message = "{member.auth.token.empty}")
    private String token;

}
