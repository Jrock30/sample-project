package com.sample.project.common.wrapper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : user
 * @desc : 공통 Response
 * @since : 2022/11/01
 */
@Getter
@Setter
public class CommonSuccessResponse<T> {

    private final boolean result = true;
    private String message;
    private T data;

    public CommonSuccessResponse() {
        this.data = null;
        this.message = "SUCCESS";
    }
    public CommonSuccessResponse(T data) {
        this.data = data;
        this.message = "SUCCESS";
    }

    public CommonSuccessResponse(String message) {
        this.data = null;
        this.message = message;
    }

    public CommonSuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

}
