package com.sample.project.common.wrapper;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommonPageSuccessResponse<T> extends CommonSuccessResponse<T> {

    private Pagination pagination;
    private Object optionData;


    public CommonPageSuccessResponse(T result, Pagination pagination) {
        super(result);
        this.pagination = pagination;
    }
}
