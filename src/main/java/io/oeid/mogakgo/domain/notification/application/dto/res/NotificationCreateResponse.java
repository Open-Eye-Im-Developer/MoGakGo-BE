package io.oeid.mogakgo.domain.notification.application.dto.res;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationCreateResponse {

    private final Long id;
    private final Long userId;
    private final NotificationTag notificationTag;
    private final String detailData;
    private final LocalDateTime createdAt;
    private final Boolean checkedYn;

    public static NotificationCreateResponse from(Notification notification) {
        return new NotificationCreateResponse(
            notification.getId(),
            notification.getUser().getId(),
            notification.getNotificationTag(),
            notification.getDetailData(),
            notification.getCreatedAt(),
            notification.getCheckedYn());
    }
}
