package io.oeid.mogakgo.domain.notification.presentation.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림 데이터")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class NotificationDataVo {

    @Schema(description = "수신자 ID", nullable = true)
    private Long receiverId;
    @Schema(description = "프로젝트 ID", nullable = true)
    private Long projectId;
    @Schema(description = "업적 ID", nullable = true)
    private Long achievementId;

    public static NotificationDataVo from(Notification notification) {
        return new NotificationDataVo(
            notification.getReceiver() == null ? null : notification.getReceiver().getId(),
            notification.getProject() == null ? null : notification.getProject().getId(),
            notification.getAchievement() == null ? null : notification.getAchievement().getId()
        );
    }
}
