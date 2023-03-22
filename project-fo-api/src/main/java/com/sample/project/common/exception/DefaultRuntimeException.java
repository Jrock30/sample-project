package com.sample.project.common.exception;

import com.sample.project.common.type.ResponseErrorCode;

public class DefaultRuntimeException extends RuntimeException {

    private final String message;
    private final ResponseErrorCode code;

    public DefaultRuntimeException(String message) {
        this.code = ResponseErrorCode.FAIL_500; // 기본 코드값
        this.message = message;
    }

    public DefaultRuntimeException(ResponseErrorCode code, String message) { this.code = code; this.message = message; }

    public DefaultRuntimeException(ResponseErrorCode code) { this.code = code; this.message = code.message(); }

    public ResponseErrorCode getCode() { return this.code; }

    public String getMessage() {
        return this.message;
    }

}
