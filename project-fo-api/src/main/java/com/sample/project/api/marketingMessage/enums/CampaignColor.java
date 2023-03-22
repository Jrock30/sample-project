package com.sample.project.api.marketingMessage.enums;
/**
 * 마케팅 메시지>캠페인 컬러 코드 enum
 */
public enum CampaignColor {

    FFE1E1("#FFE1E1", "다홍색"),
    FFDBD0("#FFDBD0", "빨간색"),
    FFEAD1("#FFEAD1", "주황색"),
    FFF8CC("#FFF8CC", "노란색"),
    EDF7D3("#EDF7D3", "연두색"),
    E3F8E3("#E3F8E3", "초록색"),
    D5EAFD("#D5EAFD", "하늘색"),
    D0E0FF("#D0E0FF", "파란색"),
    E7DEFF("#E7DEFF", "보라색"),
    FFE9FD("#FFE9FD", "분홍색"),
    ;

    CampaignColor(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    private final String code;
    private final String codeName;



    public String code() {
        return code;
    }

    public String codeName() {
        return codeName;
    }

}
