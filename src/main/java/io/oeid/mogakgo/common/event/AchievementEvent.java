package io.oeid.mogakgo.common.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AchievementEvent {

    protected Long userId;
    protected Long achievementId;
}
