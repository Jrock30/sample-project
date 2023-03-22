package com.sample.project.api.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Schema(description = "핸드폰 번호 변경 DTO")
public class RequestChangeMobileDto {

    @Schema(description = "핸드폰 번호", required = true, example = "01012345678")
    @NotEmpty(message = "{member.auth.empty.phone.number}")
    private String mobile;

    @Schema(description = "인증번호", required = true, example = "123456")
    @NotEmpty(message = "{member.empty.auth.code}")
    @Size(min = 8, max = 32)
    private String authNumber;
}
