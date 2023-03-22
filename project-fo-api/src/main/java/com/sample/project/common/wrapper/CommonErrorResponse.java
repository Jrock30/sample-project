package com.sample.project.common.wrapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "CommonErrorResponse", description = "Response Error 리턴값 방출 래핑 클래스")
@AllArgsConstructor
public class CommonErrorResponse {

	private final boolean result = false;

	private Integer errorCode;

	private String errorMessage;
}
