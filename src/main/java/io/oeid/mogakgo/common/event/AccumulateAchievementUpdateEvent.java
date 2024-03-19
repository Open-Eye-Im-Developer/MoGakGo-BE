package io.oeid.mogakgo.common.event;

import lombok.Builder;

public class AccumulateAchievementUpdateEvent extends AchievementEvent {

    @Builder
    public AccumulateAchievementUpdateEvent(Long userId, Long achievementId) {
        super(userId, achievementId);
    }
}
