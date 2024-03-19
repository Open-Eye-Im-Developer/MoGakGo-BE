package io.oeid.mogakgo.domain.achievement.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AchievementMessage {

    private final Long userId;
    private final Long achievementId;
    private final Integer progressCount;
    private final Boolean completed;

    @Builder
    private AchievementMessage(Long userId, Long achievementId, Integer progressCount, Boolean completed) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.progressCount = progressCount;
        this.completed = completed;
    }

}
