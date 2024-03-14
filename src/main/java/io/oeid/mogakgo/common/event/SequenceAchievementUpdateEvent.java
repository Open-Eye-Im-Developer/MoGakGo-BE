package io.oeid.mogakgo.common.event;

import lombok.Builder;

public class SequenceAchievementUpdateEvent extends AchievementEvent {

    @Builder
    public SequenceAchievementUpdateEvent(Long userId, Long achievementId) {
        super(userId, achievementId);
    }
}
