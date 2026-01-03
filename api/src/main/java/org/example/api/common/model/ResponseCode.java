package org.example.api.common.model;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ResponseCode {

    /**
     * 2xx
     */
    SUCCESS(HttpStatus.OK, "요청을 성공적으로 처리했습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입에 성공했습니다"),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),

    /**
     * 4xx
     */
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "유효한 인증 정보가 없습니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 리소스의 현재 상태와 충돌합니다."),

    /**
     * 5xx
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "외부 서비스와 통신 중 오류가 발생했습니다."),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "외부 요청 서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
