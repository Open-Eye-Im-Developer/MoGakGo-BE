package io.oeid.mogakgo.core.properties.event.vo;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AchievementEvent extends GeneralEvent {

    private Object target;

    @Builder
    public AchievementEvent(Long userId, ActivityType activityType, Object target) {
        super(userId, activityType);
        this.target = target;
    }
}
