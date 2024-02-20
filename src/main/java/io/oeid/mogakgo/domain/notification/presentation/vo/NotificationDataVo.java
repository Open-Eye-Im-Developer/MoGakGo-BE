package io.oeid.mogakgo.domain.notification.presentation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림 데이터")
@Getter
@AllArgsConstructor
public class NotificationDataVo {

    private final String detail;
    private final LocalDateTime createdAt;

}
