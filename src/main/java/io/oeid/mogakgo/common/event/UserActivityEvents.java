package io.oeid.mogakgo.common.event;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserActivityEvents extends Event {

    @Builder
    private UserActivityEvents(Long userId, ActivityType activityType) {
        super(userId, activityType);
    }

}
