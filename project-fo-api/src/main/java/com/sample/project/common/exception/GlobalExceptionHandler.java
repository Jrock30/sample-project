package com.sample.project.common.exception;

import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.wrapper.CommonErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().excluding(ErrorAttributeOptions.Include.EXCEPTION));
            }
        };
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonErrorResponse> handleCustomException(CustomException e) {
        log.error("====== handleCustomException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(e.getHttpStatus().value(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(cem);
    }

    @ExceptionHandler(DefaultRuntimeException.class)
    public ResponseEntity<?> handleDefaultRuntimeException(DefaultRuntimeException e) {
        log.error("====== handleDefaultRuntimeException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(e.getCode().code(), e.getCode().message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cem);
    }

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<?> handleDefaultException(DefaultException e) {
        log.error("====== handleDefaultException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(e.getCode().code(), e.getCode().message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cem);
    }

    @ExceptionHandler(RequestValidateException.class)
    public ResponseEntity<CommonErrorResponse> handleRequestValidateException(RequestValidateException e) {
        log.error("====== handleRequestValidateException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        String message = e.getErrors().getFieldErrors().get(0).getDefaultMessage();
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_400.code(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<CommonErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.error("====== handleBadCredentialsException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_400.code(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public ResponseEntity<CommonErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error("====== handleInternalAuthenticationServiceException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_4011.code(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cem);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("====== handleHttpRequestMethodNotSupportedException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_405.code(), FAIL_405.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("====== handleMissingServletRequestParameterException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_400.code(), "Please check the parameters { " + e.getParameterName() + " }");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<CommonErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("====== handleAccessDeniedException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_403.code(), FAIL_400.message());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(cem);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<CommonErrorResponse> handleHttpClientErrorException(HttpClientErrorException e) {
        log.error("====== httpClientErrorException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_400.code(), FAIL_400.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("====== handleHttpMessageNotReadableException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_4000.code(), FAIL_4000.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cem);
    }

    @ExceptionHandler({HttpServerErrorException.class})
    public ResponseEntity<CommonErrorResponse> handleHttpServerErrorException(HttpServerErrorException e) {
        log.error("====== handleHttpServerErrorException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_500.code(), FAIL_500.message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cem);
    }

    @ExceptionHandler({RestClientException.class})
    public ResponseEntity<CommonErrorResponse> handleRestClientException(RestClientException e) {
        log.error("====== handleRestClientException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_500.code(), FAIL_500.message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorResponse> handleException(Exception e) {
        log.error("====== handleException ====== \n {}", CommonUtils.getPrintStackTrace(e));
        CommonErrorResponse cem = new CommonErrorResponse(FAIL_500.code(), FAIL_500.message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cem);
    }

}