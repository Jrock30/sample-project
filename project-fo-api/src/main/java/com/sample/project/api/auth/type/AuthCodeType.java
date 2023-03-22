package com.sample.project.api.auth.type;

public enum AuthCodeType {

    AUTH_TYPE_MAIL("MAIL"),
    AUTH_TYPE_MOBILE("MOBILE");

    private final String code;

    AuthCodeType(String code) {
        this.code = code;
    }

    public String code(){return code;}
}
