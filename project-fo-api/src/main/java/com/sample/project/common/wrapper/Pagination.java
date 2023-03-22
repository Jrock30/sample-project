package com.sample.project.common.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;


@Getter
@Setter
@NoArgsConstructor
public class Pagination {

    private Integer page; // 페이지번호
    private Integer size; // 페이지에 보여줄 row 수
    private Long total; // 전체 건 수

    public Pagination(Page<?> page) {
        this.page = page.getNumber();
        this.size = page.getSize();
        this.total = page.getTotalElements();
    }

}
