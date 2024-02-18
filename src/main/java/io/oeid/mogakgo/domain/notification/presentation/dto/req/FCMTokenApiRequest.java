package io.oeid.mogakgo.domain.notification.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMTokenApiRequest {

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private final String fcmToken;
}
