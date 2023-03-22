package com.sample.project.common.wrapper;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommonMultipleTypePageSuccessResponse<T,S> extends CommonSuccessResponse<T> {

    private Pagination pagination;
    private S overviewData;

    public CommonMultipleTypePageSuccessResponse(T result, Pagination pagination, S overviewData) {
        super(result);
        this.pagination = pagination;
        this.overviewData = overviewData;
    }

}
