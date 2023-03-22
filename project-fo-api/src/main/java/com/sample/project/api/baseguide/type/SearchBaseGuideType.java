package com.sample.project.api.baseguide.type;

public enum SearchBaseGuideType {
    CONTENT("content", "응답내용"),
    CATEGORY("catName", "분류명"),
    ;

    SearchBaseGuideType(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }
    private final String code;
    private final String codeName;

    public String code() {
        return code;
    }
}
