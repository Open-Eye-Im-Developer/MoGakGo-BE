package io.oeid.mogakgo.domain.notification.application.dto.req;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationCreateRequest {

    private final Long userId;
    private final NotificationTag notificationTag;
    private final String detailData;

    public static NotificationCreateRequest of(Long userId, NotificationTag notificationTag,
        String detailData) {
        return new NotificationCreateRequest(userId, notificationTag, detailData);
    }

    public Notification toEntity(User user) {
        return Notification.of(user, notificationTag, detailData);
    }
}
