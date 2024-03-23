package io.oeid.mogakgo.common.event;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AchievementCompletionEvent extends Event {

    private final Object target;

    @Builder
    private AchievementCompletionEvent(Long userId, ActivityType activityType, Object target) {
        super(userId, activityType);
        this.target = target;
    }
}
