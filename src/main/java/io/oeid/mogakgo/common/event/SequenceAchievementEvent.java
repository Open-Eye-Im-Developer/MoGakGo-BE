package io.oeid.mogakgo.common.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SequenceAchievementEvent extends AchievementEvent {

    private final Boolean completed;

    @Builder
    public SequenceAchievementEvent(Long userId, Long achievementId, Boolean completed) {
        super(userId, achievementId);
        this.completed = completed;
    }
}
