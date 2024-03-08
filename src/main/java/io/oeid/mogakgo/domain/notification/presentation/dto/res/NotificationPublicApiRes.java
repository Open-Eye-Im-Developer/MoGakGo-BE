package io.oeid.mogakgo.domain.notification.presentation.dto.res;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import io.oeid.mogakgo.domain.notification.presentation.vo.NotificationDataVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림 API 응답")
@Getter
@AllArgsConstructor
public class NotificationPublicApiRes {

    @Schema(description = "알림 ID")
    private Long id;

    @Schema(description = "알림 형식")
    private NotificationTag tag;

    @Schema(description = "알림 메시지")
    private String message;

    private NotificationDataVo data;

    @Schema(description = "알림 생성 시간")
    private LocalDateTime createdAt;

    public static NotificationPublicApiRes from(Notification notification) {
        return new NotificationPublicApiRes(
            notification.getId(),
            notification.getNotificationTag(),
            notification.getMessage(),
            NotificationDataVo.from(notification),
            notification.getCreatedAt()
        );
    }
}
