package com.sample.project.security.type;

public enum RoleType {

    /**
     * 권한
     */
    SUPER("ROLE_SUPER", "슈퍼관리자"),
    ADMIN("ROLE_ADMIN", "관리자"),
    MASTER("ROLE_MASTER", "마스터사용자"),
    USER("ROLE_USER", "일반사용자")
    ;

    private final String code;
    private final String desc;
    public String toString;

    RoleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String code() {
        return code;
    }

    public String desc() {
        return desc;
    }

    /**
     * SimpleGrantedAuthority 을 구성하여 반환
     */
//    public SimpleGrantedAuthority getAuthority() {
//        return new SimpleGrantedAuthority("ROLE_" + name());
//    }

}
