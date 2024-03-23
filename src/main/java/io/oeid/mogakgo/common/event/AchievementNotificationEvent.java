package io.oeid.mogakgo.common.event;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AchievementNotificationEvent extends Event {

    private final Object target;

    @Builder
    private AchievementNotificationEvent(Long userId, ActivityType activityType, Object target) {
        super(userId, activityType);
        this.target = target;
    }
}
