package io.oeid.mogakgo.domain.notification.domain.enums;

import lombok.Getter;

@Getter
public enum FCMNotificationType {
    MATCHING_SUCCEEDED("/project"),
    REVIEW_REQUEST("/review"),
    ;

    private final String redirectUri;

    FCMNotificationType(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
