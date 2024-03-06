package io.oeid.mogakgo.domain.notification.application.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "알림 확인 응답")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCheckedRes {

    @Schema(description = "알림 ID", example = "1", implementation = Long.class)
    private Long notificationId;
}
