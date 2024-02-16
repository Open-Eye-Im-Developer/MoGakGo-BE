package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode404 implements ErrorCode {
    USER_NOT_FOUND("E1100", "해당 유저가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private final String code;
    private final String message;

    ErrorCode404(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
