package io.oeid.mogakgo.core.properties.event.vo;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AchievementEvent extends GeneralEvent {

    private final Object target;

    @Builder
    private AchievementEvent(Long userId, ActivityType activityType, Object target) {
        super(userId, activityType);
        this.target = target;
    }
}
