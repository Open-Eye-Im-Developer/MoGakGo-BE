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

    private final Long senderId;
    private final Long receiverId;
    private final NotificationTag notificationTag;
    private final String detailData;

    public static NotificationCreateRequest of(Long senderId, Long receiverId,
        NotificationTag notificationTag,
        String detailData) {
        return new NotificationCreateRequest(senderId, receiverId, notificationTag, detailData);
    }

    public Notification toEntity(User sender, User receiver) {
        return Notification.of(sender, receiver, notificationTag, detailData);
    }
}
