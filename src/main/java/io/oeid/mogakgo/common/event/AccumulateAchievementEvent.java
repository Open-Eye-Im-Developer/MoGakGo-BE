package io.oeid.mogakgo.common.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccumulateAchievementEvent extends AchievementEvent {

    private final Boolean completed;

    @Builder
    public AccumulateAchievementEvent(Long userId, Long achievementId, Boolean completed) {
        super(userId, achievementId);
        this.completed = completed;
    }
}
