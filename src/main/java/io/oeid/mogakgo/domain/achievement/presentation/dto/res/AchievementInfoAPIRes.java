package io.oeid.mogakgo.domain.achievement.presentation.dto.res;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "업적 상세 정보 조회 응답 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AchievementInfoAPIRes {

    @Schema(description = "조회할 업적 ID")
    private final Long achievementId;

    @Schema(description = "조회할 업적 타이틀")
    private final String title;

    @Schema(description = "조회할 업적 이미지 Url")
    private final String imgUrl;

    @Schema(description = "조회할 업적 상세 설명")
    private final String description;

    @Schema(description = "조회할 업적 타입")
    private final RequirementType requirementType;

    @Schema(description = "업적 달성을 위해 만족해야 하는 값")
    private final Integer requirementValue;

    public static AchievementInfoAPIRes from(Achievement achievement) {
        return new AchievementInfoAPIRes(
            achievement.getId(),
            achievement.getTitle(),
            achievement.getImgUrl(),
            achievement.getDescription(),
            achievement.getRequirementType(),
            achievement.getRequirementValue()
        );
    }

}
