package io.oeid.mogakgo.domain.notification.presentation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림 발신자 정보")
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class NotificationSenderVo {
    @Schema(description = "알림 전송자 이름", defaultValue = "mogakgo")
    private String name;
    @Schema(description = "알림 전송자 아이디", nullable = true)
    private Long id;
    @Schema(description = "알림 전송자 프로필 이미지 URL", nullable = true)
    private String profileImageUrl;

}
