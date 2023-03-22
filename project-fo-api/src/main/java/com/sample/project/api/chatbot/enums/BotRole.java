package com.sample.project.api.apple.enums;

import lombok.Getter;

@Getter
public enum BotRole {

    BOT_MASTER("MASTER", "마스터"),
    BOT_MANAGER("MANAGER", "운영자")
    ;

    private final String code;
    private final String codeName;

    BotRole(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    public String code(){return this.code;}
    public String codeName(){return this.codeName;}
}
