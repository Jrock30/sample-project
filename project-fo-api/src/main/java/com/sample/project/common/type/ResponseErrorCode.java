package com.sample.project.common.type;

import org.springframework.http.HttpStatus;

/**
 * 공통 에러 응답코드
 */
public enum ResponseErrorCode {

    /**
     * HTTP 상태 코드
     */
    FAIL_400(400, "요청이 잘못 되었습니다.", HttpStatus.BAD_REQUEST),
    FAIL_401(401, "로그인을 해주세요.", HttpStatus.FORBIDDEN),
    FAIL_403(403, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FAIL_404(404, "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FAIL_405(405, "리소스를 찾을 수 없습니다.", HttpStatus.METHOD_NOT_ALLOWED),
    FAIL_500(500, "서버 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Custom 코드
     */
    FAIL_4000(4000, "입력값을 확인해주세요."),
    FAIL_4001(4001, "유효하지 않는 토큰입니다."),
    FAIL_4002(4002, "계정정보가 틀립니다."),
    FAIL_4003(4003, "몰 정보를 찾을 수 없습니다."),
    FAIL_4004(4004, "Apple24 앱을 통해 접근해주세요."),
    FAIL_4005(4005, "해당 봇 정보를 찾을 수 없습니다."),
    FAIL_4006(4006, "회원정보를 찾을 수 없습니다."),
    FAIL_4007(4007, "등록되지 않은 아이디 입니다."),
    FAIL_4008(4008, "1일 요청 가능 횟수를 초과하였습니다. 내일 다시 시도해 주세요."),
    FAIL_4009(4009, "비밀번호가 일치하지 않습니다. 다시 입력해주세요."),
    FAIL_4010(4010, "해당 정보가 없습니다."),
    FAIL_4011(4011, "가입이 완료되지 않았습니다."),
    FAIL_4012(4012, "잠시 후 다시 요청해주세요. (최대 3분)"),
    FAIL_4013(4013, "등록되지 않은 전화번호입니다."),
    FAIL_4014(4014, "인증번호가 틀립니다. 다시 입력 해주세요."),
    FAIL_4015(4015, "인증토큰이 유효하지 않습니다. 다시 요청해주세요."),
    FAIL_4016(4016, "대표 운영자만 생성할 수 있습니다. \n Apple24를 통해 접근해 주세요."),
    FAIL_4017(4017, "Apple24 아이디 정보를 찾을 수 없습니다."),
    FAIL_4018(4018, "카카오 검색 아이디 정보를 찾을 수 없습니다."),
    FAIL_4019(4019, "어드민 키를 찾을 수 없습니다."),
    FAIL_4020(4020, "위임-대행 상태가 유효하지 않습니다."),
    FAIL_4021(4021, "봇을 선택해주세요."),
    FAIL_4022(4022, "같은 비밀번호로 변경할 수 없습니다."),
    FAIL_4023(4023, "변경할 비밀번호가 일치하지 않습니다."),
    FAIL_4024(4024, "카페24를 통한 접근이 2시간이 지났습니다. 카페24를 통해 다시 접근해 주세요."),
    FAIL_4025(4025, "인증 정보가 없습니다."),
    FAIL_4026(4026, "모바일 인증을 해주세요."),
    FAIL_4027(4027, "로그인 5회 실패 하였습니다. 30분 후 다시 시도해주세요."),
    FAIL_4028(4028, "이미 가입이 완료 되었습니다."),
    FAIL_4029(4029, "이미 등록된 휴대폰 번호 입니다."),
    FAIL_4030(4030, "인증 시간 3분이 초과하였습니다. 인증번호를 다시 요청 하세요."),
    FAIL_4100(4100, "비즈톡 토큰이 존재하지 않습니다."),
    FAIL_5000(5000, "알 수 없는 에러가 발생하였습니다."),
    FAIL_6000(6000,"이미 생성된 봇이 있습니다"),
    FAIL_6001(6001,"위임봇 생성에 실패하였습니다"),
    FAIL_6002(6002,"대행 중인 봇은 삭제가 불가능합니다"),
    FAIL_6003(6003,"이미 대행 중인 봇 입니다."),
    FAIL_6004(6004,"철회 할 수 없는 상태 입니다."),
    FAIL_6005(6005,"마스터만 요청할 수 있습니다."),
    FAIL_6006(6006,"마스터만 수락할 수 있습니다."),
    FAIL_6007(6007,"마스터만 거절할 수 있습니다."),
    FAIL_6008(6008,"챗봇 요청 진행이 완료되지 않았습니다."),
    FAIL_6009(6009,"이미 위임-대행 요청중인 봇 입니다."),
    FAIL_6300(6300,"연결할 봇의 채널 아이디가 존재하지 않습니다"),
    FAIL_7007(7007, "해당 검색결과를 찾을 수 없습니다."),
    FAIL_7008(7008, "엑셀파일에서 B, C, D열을 삭제하지 않았습니다."),
    FAIL_7009(7009, "응답내용의 글자수가 초과되었습니다."),
    FAIL_7010(7010, "동일 코드 중복입니다."),
    FAIL_7011(7011, "존재하지 않는 코드입니다."),
    FAIL_7012(7012, "해당 분류를 찾을 수 없습니다."),
    FAIL_7013(7013, "엑셀 시트의 B, C, D, E열이 삭제되지 않았습니다."),
    FAIL_7014(7014, "해당 블록코드에 맞는 도움말이 없습니다."),
    FAIL_7015(7015, "엑셀파일에서 B, C, D, E, F열을 삭제하지 않았습니다."),
    FAIL_7016(7016, "입력 가능한 글자수가 초과되었습니다."),
    FAIL_7017(7017, "분류 조건 선택이 잘못되었습니다."),
    FAIL_7018(7018, "엑셀파일만 업로드 해주세요."),
    FAIL_7019(7019, "엑셀파일에 공백인 데이터가 있습니다."),
    FAIL_7020(7020, "해당 사용자가 운영자로 있는 봇의 개수가 5개가 넘습니다."),
    FAIL_7021(7021, "엑셀파일의 데이터 형식이 잘못되었습니다."),
    FAIL_7022(7022, "메세지 발송결과 번호가 올바르지 않습니다.");

    ResponseErrorCode(Integer code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    ResponseErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private final Integer code;

    private final String message;

    private final HttpStatus status;

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    public HttpStatus status() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("Code:%s, Message:%s", code(), message());
    }

}
