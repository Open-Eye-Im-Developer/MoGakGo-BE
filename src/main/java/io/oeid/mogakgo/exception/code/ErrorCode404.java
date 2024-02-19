package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode404 implements ErrorCode {
    USER_NOT_FOUND("E020301", "해당 유저가 존재하지 않습니다."),
    PROJECT_NOT_FOUND("E030301", "해당 프로젝트가 존재하지 않습니다."),
    NOTIFICATION_FCM_TOKEN_NOT_FOUND("E060301", "해당 유저의 FCM 토큰이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private final String code;
    private final String message;

    ErrorCode404(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
