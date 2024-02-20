package io.oeid.mogakgo.domain.notification.presentation.dto.res;

import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import io.oeid.mogakgo.domain.notification.presentation.vo.NotificationDataVo;
import io.oeid.mogakgo.domain.notification.presentation.vo.NotificationSenderVo;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private NotificationSenderVo sender;
    private NotificationDataVo data;

}
