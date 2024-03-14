package io.oeid.mogakgo.common.event;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserActivityEvent {

    private final Long userId;
    private final ActivityType activityType;

    @Builder
    private UserActivityEvent(Long userId, ActivityType activityType) {
        this.userId = userId;
        this.activityType = activityType;
    }

}
