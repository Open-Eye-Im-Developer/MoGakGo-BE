package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
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

    @Schema(description = "대표 업적 ID")
    private final Long achievementId;

    @Schema(description = "대표 업적 타이틀")
    private final String title;

    @Schema(description = "대표 업적 이미지 Url")
    private final String imgUrl;

    @Schema(description = "대표 업적 설명")
    private final String description;

    @Schema(description = "대표 업적 달성 타입")
    private final RequirementType requirementType;

    @Schema(description = "대표 업적 달성 조건")
    private final Integer requirementValue;

    @Schema(description = "대표 업적 진행 횟수")
    private final Integer progressCount;

    @Schema(description = "대표 업적 달성 여부")
    private final Boolean completed;

    public static UserAchievementUpdateApiResponse from(UserAchievementInfoRes response) {
        return new UserAchievementUpdateApiResponse(
            response.getUserId(),
            response.getAchievementId(),
            response.getTitle(),
            response.getImgUrl(),
            response.getDescription(),
            response.getRequirementType(),
            response.getRequirementValue(),
            response.getProgressCount(),
            response.getCompleted()
        );
    }
}
