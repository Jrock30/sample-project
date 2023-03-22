package com.sample.project.common.exception;

import com.sample.project.common.type.ResponseErrorCode;

public class DefaultException extends Exception {

    private final String message;

    private final ResponseErrorCode code;

    public DefaultException(String message) {
        this.code = ResponseErrorCode.FAIL_500; // 기본 코드값
        this.message = message;
    }

    public DefaultException(ResponseErrorCode code, String message) { this.code = code; this.message = message; }

    public DefaultException(ResponseErrorCode code) { this.code = code; this.message = code.message(); }

    public ResponseErrorCode getCode() { return this.code; }

    public String getMessage() {
        return this.message;
    }

}
