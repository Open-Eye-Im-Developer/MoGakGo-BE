package io.oeid.mogakgo.core.properties.event.vo;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GeneralEvent {

    protected Long userId;
    protected ActivityType activityType;

}
