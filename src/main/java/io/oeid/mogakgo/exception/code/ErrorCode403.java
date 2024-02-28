package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode403 implements ErrorCode {
    AUTH_ACCESS_DENIED("E010203", "접근 권한이 없습니다."),
    PROJECT_FORBIDDEN_OPERATION("E030201", "해당 프로젝트에 대한 권한이 없습니다."),
    PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION("E050201", "해당 프로젝트 요청에 대한 권한이 없습니다."),
    INVALID_CERT_INFORMATION("E070201", "동네 인증을 수행할 권한이 없습니다."),
    PROFILE_CARD_LIKE_FORBIDDEN_OPERATION("E040101", "본인 프로필 카드의 좋아요 수만 조회할 수 있습니다."),
    MATCHING_FORBIDDEN_OPERATION("E090201", "해당 매칭에 대한 권한이 없습니다."),
    USER_FORBIDDEN_OPERATION("E20201", "사용자 정보를 수정할 권한이 없습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    private final String code;
    private final String message;

    ErrorCode403(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
