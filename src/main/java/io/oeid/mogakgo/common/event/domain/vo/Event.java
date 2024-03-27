package io.oeid.mogakgo.common.event.domain.vo;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Event {

    protected Long userId;
    protected ActivityType activityType;

}
