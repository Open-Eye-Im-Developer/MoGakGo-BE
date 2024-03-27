package io.oeid.mogakgo.common.event.handler;

import io.oeid.mogakgo.common.event.domain.vo.UserActivityEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
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
public class UserActivityListener {

    private static final Integer MIN_PROGRESS_SIZE = 1;

    private final UserCommonService userCommonService;
    private final AchievementJpaRepository achievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final AchievementFacadeService achievementFacadeService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final UserActivityEvent event) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(event.getUserId(), event.getActivityType());

        if (achievementId != null) {

            // -- 업적이 연속적으로 달성 가능한 업적인 경우
            Achievement achievement = achievementFacadeService.getById(achievementId);
            if (achievement.getRequirementType().equals(RequirementType.SEQUENCE)) {

                // -- 당일에 연속적으로 달성 가능한 업적에 대한 이벤트를 발행한 적이 없는 경우
                if (achievementFacadeService.validateActivityDuplicate(event.getUserId(), event.getActivityType())) {
                    saveActivity(event);
                }
            } else {

                // -- 한 번에 달성 가능한 업적이 아닌 경우
                if (!isAvailableToAchieveOnce(event.getActivityType())) {
                    saveActivity(event);
                }
            }
        }
    }

    private void saveActivity(final UserActivityEvent event) {
        User user = userCommonService.getUserById(event.getUserId());
        userActivityRepository.save(
            UserActivity.builder()
                .user(user)
                .activityType(event.getActivityType())
                .build()
        );
    }

    private boolean isAvailableToAchieveOnce(ActivityType activityType) {
        return getProgressLevelSize(activityType).equals(MIN_PROGRESS_SIZE);
    }

    private Integer getProgressLevelSize(ActivityType activityType) {
        return achievementRepository.findByActivityType(activityType).size();
    }

}
