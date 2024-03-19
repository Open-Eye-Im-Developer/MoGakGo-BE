package io.oeid.mogakgo.common.event;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserActivityEvent {

    private final Long userId;
    private final Long achievementId;
    private final ActivityType activityType;
    private final Integer progressCount;

    @Builder
    private UserActivityEvent(Long userId, Long achievementId, ActivityType activityType,
        Integer progressCount) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.activityType = activityType;
        this.progressCount = progressCount;
    }

}
