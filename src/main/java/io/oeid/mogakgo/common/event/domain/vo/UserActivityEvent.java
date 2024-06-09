package io.oeid.mogakgo.common.event.domain.vo;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserActivityEvent extends Event {

    @Builder
    private UserActivityEvent(Long userId, ActivityType activityType) {
        super(userId, activityType);
    }

}
