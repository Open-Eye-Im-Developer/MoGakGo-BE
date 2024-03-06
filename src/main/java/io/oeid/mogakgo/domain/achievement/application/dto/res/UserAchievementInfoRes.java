package io.oeid.mogakgo.domain.achievement.application.dto.res;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAchievementInfoRes {

    private final Long userId;
    private final Long achievementId;
    private final String title;
    private final String imgUrl;
    private final String description;
    private final RequirementType requirementType;
    private final Integer requirementValue;
    private final Integer progressCount;
    private final Boolean completed;
}
