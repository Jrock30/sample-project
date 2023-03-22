package com.sample.project.api.login.type;

import lombok.Getter;

/**
 * @author    : user
 * @since     : 2022/11/29
 * @desc      : 회원검색 기간 구분코드
 */

@Getter
public enum MemberSearchDateType {

    MEMBER_SEARCH_DATE_TYPE_01("MEMBER_SEARCH_DATE_TYPE_01","선택안함"),
    MEMBER_SEARCH_DATE_TYPE_02("MEMBER_SEARCH_DATE_TYPE_02","가입일"),
    MEMBER_SEARCH_DATE_TYPE_03("MEMBER_SEARCH_DATE_TYPE_03","가입초대일"),
    MEMBER_SEARCH_DATE_TYPE_04("MEMBER_SEARCH_DATE_TYPE_04","정지일"),
    MEMBER_SEARCH_DATE_TYPE_05("MEMBER_SEARCH_DATE_TYPE_05","탈퇴일")
    ;

    MemberSearchDateType(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;

    public String code(){return code;}
    public String codeName(){return codeName;}
}
