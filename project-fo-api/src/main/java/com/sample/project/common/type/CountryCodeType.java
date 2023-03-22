package com.sample.project.common.type;

public enum CountryCodeType {

    COUNTRY_KR("82", "한국");

    CountryCodeType(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;

    public String code(){return code;}

    public String codeName(){return codeName;}

}
