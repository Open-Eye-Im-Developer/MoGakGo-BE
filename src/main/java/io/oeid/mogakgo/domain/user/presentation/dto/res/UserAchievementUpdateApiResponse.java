package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자의 대표 업적 수정 요청의 응답 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAchievementUpdateApiResponse {

    @Schema(description = "대표 업적을 수정한 사용자 ID", example = "11", implementation = Long.class)
    @NotNull
    private final Long userId;

    public static UserAchievementUpdateApiResponse from(Long userId) {
        return new UserAchievementUpdateApiResponse(userId);
    }
}
