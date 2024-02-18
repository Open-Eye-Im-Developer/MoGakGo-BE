package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode403 implements ErrorCode {
    PROJECT_FORBIDDEN_OPERATION("E030201", "해당 프로젝트에 대한 권한이 없습니다."),
    PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION("E050201", "해당 프로젝트 요청에 대한 권한이 없습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    private final String code;
    private final String message;

    ErrorCode403(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
