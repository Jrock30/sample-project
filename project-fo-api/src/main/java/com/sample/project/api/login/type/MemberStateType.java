package com.sample.project.api.login.type;

import lombok.Getter;

/**
 * @author    : user
 * @since     : 2022/11/29
 * @desc      : 회원상태 코드
 */

@Getter
public enum MemberStateType {
    // 운영자관리에서 초대는 됐지만 메일받고 회원가입은 안된 상태
    MEMBER_STATE_UNCERTIFIED("UNCERTIFIED","미인증"),
    MEMBER_STATE_NORMAL("NORMAL","정상"),
    MEMBER_STATE_SUSPENSION("SUSPENSION","정지"),
    MEMBER_STATE_WITHDRAWAL("WITHDRAWAL","탈퇴")
    ;

    MemberStateType(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;

    public String code(){return code;}
    public String codeName(){return codeName;}
}
