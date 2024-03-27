package io.oeid.mogakgo.common.event.handler;


import io.oeid.mogakgo.common.event.domain.vo.AchievementNotificationEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;
    private final AchievementProgressService achievementProgressService;
    private final AchievementFacadeService achievementFacadeService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AchievementNotificationEvent event) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(event.getUserId(), event.getActivityType());

        if (achievementId != null) {

            Achievement achievement = achievementFacadeService.getById(achievementId);
            Object progressCount = event.getTarget() == null
                ? getProgressCountForAchievement(event.getUserId(), achievement) + 1
                : event.getTarget();

            // -- 업적이 달성 가능한 조건을 만족했을 경우
            if (validateAvailabilityToAchieve(progressCount, achievement)) {
                notificationService.createAchievementNotification(event.getUserId(), achievement);
            }
        }
    }

    private Integer getProgressCountForAchievement(Long userId, Achievement achievement) {
        if (achievement.getRequirementType().equals(RequirementType.ACCUMULATE)) {
            return achievementProgressService.getAccumulatedProgressCount(userId, achievement.getActivityType());
        }
        return achievementProgressService.getProgressCountMapWithoutToday(userId,
            List.of(achievement.getActivityType())).get(achievement.getActivityType());
    }

    private boolean validateAvailabilityToAchieve(Object target, Achievement achievement) {
        if (target instanceof Integer) {
            return Objects.equals(achievement.getRequirementValue(), target);
        } else if (target instanceof Double) {
            return achievement.getRequirementValue() <= (Double) target;
        } else {
            throw new IllegalArgumentException("Unsupported target type");
        }
    }

}
