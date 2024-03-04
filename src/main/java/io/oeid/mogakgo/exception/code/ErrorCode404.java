package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode404 implements ErrorCode {
    USER_NOT_FOUND("E020301", "해당 유저가 존재하지 않습니다."),
    PROJECT_NOT_FOUND("E030301", "해당 프로젝트가 존재하지 않습니다."),
    PROFILE_CARD_NOT_FOUND("E040301", "해당 프로필 카드가 존재하지 않습니다."),
    NOTIFICATION_FCM_TOKEN_NOT_FOUND("E060301", "해당 유저의 FCM 토큰이 존재하지 않습니다."),
    PROJECT_JOIN_REQUEST_NOT_FOUND("E050301", "해당 프로젝트 참여 요청이 존재하지 않습니다."),
    MATCHING_NOT_FOUND("E090301", "해당 매칭이 존재하지 않습니다."),
    CHAT_ROOM_NOT_FOUND("E110301", "해당 채팅방이 존재하지 않습니다."),
    ACHIEVEMENT_NOT_FOUND("E130301", "해당 업적이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private final String code;
    private final String message;

    ErrorCode404(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
