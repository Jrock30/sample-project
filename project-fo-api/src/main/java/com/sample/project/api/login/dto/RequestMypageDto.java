package com.sample.project.api.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Schema(description = "비밀번호 변경 Request")
public class RequestMypageDto {

    @Schema(description = "기존 비밀번호", required = true, example = "damiadmin12#$")
    @NotEmpty(message = "{member.empty.password}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,32}$", message = "{member.pattern.password.error}")
    @Size(min = 8, max = 32)
    private String password;

    @Schema(description = "변경할 비밀번호", required = true, example = "damiadmin12#$")
    @NotEmpty(message = "{member.empty.new.password}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,32}$", message = "{member.pattern.password.error}")
    @Size(min = 8, max = 32)
    private String newPassword;

    @NotEmpty(message = "{member.empty.password.confirm}")
    @Schema(description = "변경할 비밀번호 확인", required = true, example = "damiadmin12#$")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,32}$", message = "{member.pattern.password.error}")
    private String newPasswordConfirm;
}
