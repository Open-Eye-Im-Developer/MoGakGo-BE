package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode500 implements ErrorCode {
    INTERNAL_SERVER_ERROR("E999999", "서버에서 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    CHAT_WEB_SOCKET_ERROR("E110401", "채팅 서버와의 연결이 끊겼습니다."),
    ACHIEVEMENT_WEB_SOCKET_ERROR("E140401", "업적 서버와의 연결이 끊겼습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String code;
    private final String message;

    ErrorCode500(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
