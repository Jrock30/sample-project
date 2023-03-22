package com.sample.project.common.exception;

import org.springframework.validation.Errors;

public class RequestValidateException extends RuntimeException {

    private final Errors errors;

    public RequestValidateException(Errors errors) {
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
