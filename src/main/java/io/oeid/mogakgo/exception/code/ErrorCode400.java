package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode400 implements ErrorCode {
    PATH_PARAMETER_BAD_REQUEST("E400", "잘못된 경로 파라미터입니다."),
    INVALID_INPUT_VALUE("E401", "기본 유효성 검사에 실패하였습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final String code;
    private final String message;

    ErrorCode400(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
