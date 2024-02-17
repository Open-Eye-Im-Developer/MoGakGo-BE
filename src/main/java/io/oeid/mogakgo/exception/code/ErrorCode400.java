package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode400 implements ErrorCode {
    PATH_PARAMETER_BAD_REQUEST("E000001", "잘못된 경로 파라미터입니다."),
    INVALID_INPUT_VALUE("E000002", "기본 유효성 검사에 실패하였습니다."),
    USER_DEVELOP_LANGUAGE_BAD_REQUEST("E020101", "개발 언어는 3개까지만 등록 가능합니다."),
    USER_WANTED_JOB_BAD_REQUEST("E020102", "희망 직무는 3개까지만 등록 가능합니다."),
    USERNAME_SHOULD_BE_NOT_EMPTY("E020103", "유저 이름은 비어있을 수 없습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final String code;
    private final String message;

    ErrorCode400(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
