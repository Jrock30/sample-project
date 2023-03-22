package com.sample.project.api.marketingMessage.enums;

import lombok.Getter;

/**
 * @author   	: user
 * @since    	: 2022/11/25
 * @desc     	: 마케팅 메시지>반복 요일
 */
@Getter
public enum CampaignDayWeek {

    MON("MON","월")
    ,TUE("TUE","화")
    ,WED("WED","수")
    ,THU("THU","목")
    ,FRI("FRI","금")
    ,SAT("SAT","토")
    ,SUN("SUN","일")
    ;

    CampaignDayWeek(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;

    public String code(){return code;}
    public String codeName(){return codeName;}

}
