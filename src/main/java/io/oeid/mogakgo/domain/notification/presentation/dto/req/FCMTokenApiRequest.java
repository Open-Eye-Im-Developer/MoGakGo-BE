package io.oeid.mogakgo.domain.notification.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "FCM 토큰 등록 요청")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMTokenApiRequest {

    @Schema(description = "FCM 토큰", example = "d4774fVVUuS3wdHHnGMYEj:APA91bFjQD6WXu8z5B0bliub661jwRshGvoCafMTYkm0cX9bZCbaUIa6ybycBT8WqkEN9j-qIYgFGB2zNnNosluquDhatUZmpbst87qo0oT8P2Id39xtWV0jhXpwSdIoLyZtdD0G9s5f", implementation = String.class, type = "string")
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private final String fcmToken;
}
