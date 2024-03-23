package io.oeid.mogakgo.common.event.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;

import io.oeid.mogakgo.common.event.AchievementNotificationEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.AchievementMessage;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationListener {

    private static final String SUBSCRIBE_DESTINATIONN = "/topic/achievement/";

    private final SimpMessagingTemplate messagingTemplate;
    private final AchievementJpaRepository achievementRepository;
    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementProgressService achievementProgressService;
    private final AchievementFacadeService achievementFacadeService;

    @TransactionalEventListener
    public void executeEvent(final AchievementNotificationEvent event) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(event.getUserId(), event.getActivityType());

        if (achievementId != null) {

            UserAchievement userAchievement = getByUserAndAchievement(event.getUserId(), achievementId);
            Object progressCount = event.getTarget() == null
                ? getProgressCountForAchievement(event.getUserId(), userAchievement.getAchievement()) + 1
                : event.getTarget();

            // -- 업적이 달성 가능한 조건을 만족했을 경우
            if (validateAvailabilityToAchieve(progressCount, userAchievement.getAchievement())) {
                userAchievement.updateCompleted();

                messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                    AchievementMessage.builder()
                        .userId(event.getUserId())
                        .achievementId(achievementId)
                        .progressCount((Integer) progressCount)
                        .requirementValue(userAchievement.getAchievement().getRequirementValue())
                        .build()
                );
            }
        }
    }

    private UserAchievement getByUserAndAchievement(Long userId, Long achievementId) {
        return userAchievementRepository.findByUserAndAchievementId(userId, achievementId)
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
    }

    private Integer getProgressCountForAchievement(Long userId, Achievement achievement) {
        if (achievement.getRequirementType().equals(RequirementType.ACCUMULATE)) {
            return achievementProgressService.getAccumulatedProgressCount(userId, achievement.getActivityType());
        }
        return achievementProgressService.getProgressCountMap(userId,
            List.of(achievement.getActivityType())).get(achievement.getActivityType());
    }

    private boolean validateAvailabilityToAchieve(Object target, Achievement achievement) {
        if (target instanceof Integer) {
            return Objects.equals(achievement.getRequirementValue(), (Integer) target + 1);
        } else if (target instanceof Double) {
            return achievement.getRequirementValue() <= (Double) target;
        } else {
            throw new IllegalArgumentException("Unsupported target type");
        }
    }

}
