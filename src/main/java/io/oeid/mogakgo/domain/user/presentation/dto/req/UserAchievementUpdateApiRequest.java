package io.oeid.mogakgo.domain.user.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자의 대표 업적 수정 요청 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAchievementUpdateApiRequest {

    @Schema(description = "대표 업적을 수정하려는 사용자 ID", example = "11", implementation = Long.class)
    @NotNull
    private final Long userId;

    @Schema(description = "수정하려는 대표 업적 ID", example = "2", implementation = Long.class)
    @NotNull
    private final Long achievementId;

    public static UserAchievementUpdateApiRequest of(Long userId, Long achievementId) {
        return new UserAchievementUpdateApiRequest(userId, achievementId);
    }

}
