package io.oeid.mogakgo.domain.achievement.application.dto.res;

import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import lombok.Builder;
import lombok.Getter;

@Getter
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

    @Builder
    private UserAchievementInfoRes(Long userId, Long achievementId, String title, String imgUrl,
        String description, RequirementType requirementType, Integer requirementValue,
        Integer progressCount, Boolean completed) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.requirementType = requirementType;
        this.requirementValue = requirementValue;
        this.progressCount = progressCount;
        this.completed = completed;
    }

    public static UserAchievementInfoRes of(UserAchievement userAchievement, Integer progressCount) {
        return new UserAchievementInfoRes(
            userAchievement.getUser().getId(),
            userAchievement.getAchievement().getId(),
            userAchievement.getAchievement().getTitle(),
            userAchievement.getAchievement().getImgUrl(),
            userAchievement.getAchievement().getDescription(),
            userAchievement.getAchievement().getRequirementType(),
            userAchievement.getAchievement().getRequirementValue(),
            progressCount,
            userAchievement.getCompleted()
        );
    }

}
