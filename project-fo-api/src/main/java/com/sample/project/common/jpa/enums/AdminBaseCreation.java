package com.sample.project.common.jpa.enums;

import lombok.Getter;


@Getter
public enum AdminBaseCreation {
    SAVE("저장"),UPDATE("수정");

    String value;
    AdminBaseCreation(String value) {
        this.value = value;
    }
}
