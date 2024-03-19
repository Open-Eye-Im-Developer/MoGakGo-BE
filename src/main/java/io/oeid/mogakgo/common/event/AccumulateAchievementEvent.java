package io.oeid.mogakgo.common.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccumulateAchievementEvent extends AchievementEvent {

    private final Integer progressCount;
    private final Boolean completed;

    @Builder
    public AccumulateAchievementEvent(Long userId, Long achievementId,
        Integer progressCount, Boolean completed) {
        super(userId, achievementId);
        this.progressCount = progressCount;
        this.completed = completed;
    }
}
