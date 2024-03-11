package io.oeid.mogakgo.domain.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationMessage {
    REVIEW_REQUEST_MESSAGE("리뷰 요청", " 님과의 만남 후기를 작성해주세요!"),
    ACHIEVEMENT_MESSAGE("업적 달성"," 업적을 달성했습니다!"),
    REQUEST_ARRIVAL_MESSAGE("매칭 참여 요청", "매칭 참여 요청이 도착했습니다!"),
    MATCHING_SUCCESS_MESSAGE("매칭 성공","매칭이 성공적으로 이루어졌습니다!"),
    MATCHING_FAILED_MESSAGE("매칭 실패","매칭 요청이 거절되었어요 ㅠㅠ"),
    ;

    private final String title;
    private final String message;

    NotificationMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }
}

