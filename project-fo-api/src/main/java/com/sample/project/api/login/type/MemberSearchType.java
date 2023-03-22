package com.sample.project.api.login.type;

import lombok.Getter;

/**
 * @author    : user
 * @since     : 2022/11/29
 * @desc      : 검색어 구분 코드
 */

@Getter
public enum MemberSearchType {

    MEMBER_SEARCH_TYPE_01("MEMBER_SEARCH_TYPE_01", "상호명"),
    MEMBER_SEARCH_TYPE_02("MEMBER_SEARCH_TYPE_02", "쇼핑몰명"),
    MEMBER_SEARCH_TYPE_03("MEMBER_SEARCH_TYPE_03", "사업자등록번호"),
    MEMBER_SEARCH_TYPE_04("MEMBER_SEARCH_TYPE_04", "정산담당자이름"),
    MEMBER_SEARCH_TYPE_05("MEMBER_SEARCH_TYPE_05", "정산담당자연락처"),
    MEMBER_SEARCH_TYPE_06("MEMBER_SEARCH_TYPE_06", "정산담당자이메일"),
    MEMBER_SEARCH_TYPE_07("MEMBER_SEARCH_TYPE_07", "세금계산서이메일"),
    MEMBER_SEARCH_TYPE_08("MEMBER_SEARCH_TYPE_08", "관리자메모"),
    MEMBER_SEARCH_TYPE_09("MEMBER_SEARCH_TYPE_09", "아이디"),
    MEMBER_SEARCH_TYPE_10("MEMBER_SEARCH_TYPE_10", "마스터봇이름"),
    MEMBER_SEARCH_TYPE_11("MEMBER_SEARCH_TYPE_11", "카페24아이디"),
    MEMBER_SEARCH_TYPE_12("MEMBER_SEARCH_TYPE_12", "연락처"),
    ;

    MemberSearchType(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;

    public String code(){return code;}
    public String codeName(){return codeName;}
}
