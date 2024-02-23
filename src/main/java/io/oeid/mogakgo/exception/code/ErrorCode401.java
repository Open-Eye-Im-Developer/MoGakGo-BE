package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode401 implements ErrorCode{

    AUTH_MISSING_CREDENTIALS("E010201", "사용자의 인증 정보를 찾을 수 없습니다."),
    USER_ACCOUNT_DISABLED("E020201", "탈퇴한 사용자입니다."),
    AUTH_TOKEN_EXPIRED("E010202", "토큰이 만료되었습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private final String code;
    private final String message;

    ErrorCode401(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
