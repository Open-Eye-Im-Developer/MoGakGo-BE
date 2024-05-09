package io.oeid.mogakgo.common.event.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;

import io.oeid.mogakgo.common.event.domain.vo.AchievementCompletionEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.AchievementMessage;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AchievementCheckListener {

    private static final Integer MIN_PROGRESS_SIZE = 1;
    private static final String SUBSCRIBE_DESTINATIONN = "/topic/achievement/";

    private final UserCommonService userCommonService;
    private final AchievementJpaRepository achievementRepository;
    private final AchievementFacadeService achievementFacadeService;
    private final UserAchievementJpaRepository userAchievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final AchievementProgressService achievementProgressService;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AchievementCompletionEvent event) {

        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(event.getUserId(), event.getActivityType());

        if (achievementId != null) {

            User user = userCommonService.getUserById(event.getUserId());
            Achievement achievement = achievementFacadeService.getById(achievementId);

            Boolean isExist = achievementFacadeService
                .validateAchivementAlreadyInProgress(event.getUserId(), achievementId);

            // -- 업적에 대한 진행도가 존재하지 않을 경우
            if (isExist.equals(Boolean.FALSE)) {
                userAchievementRepository.save(
                    UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .completed(Boolean.FALSE)
                        .build()
                );
            }

            Object progressCount = event.getTarget() == null
                ? getProgressCountForAchievement(event.getUserId(), achievement) + 1
                : event.getTarget();

            // -- 업적이 달성 가능한 조건을 만족했을 경우
            if (validateAvailabilityToAchieve(progressCount, achievement)) {

                UserAchievement userAchievement = getByUserAndAchievement(event.getUserId(), achievementId);
                userAchievement.updateCompleted();

                // -- 해당 업적이 연속적으로 달성 가능한 업적인 경우
                if (achievement.getRequirementType().equals(RequirementType.SEQUENCE)) {
                    List<UserActivity> history = userActivityRepository.getActivityHistoryByActivityType(
                        event.getUserId(),
                        event.getActivityType(),
                        achievement.getRequirementValue()
                    );
                    history.forEach(UserActivity::delete);
                }

                // -- 업적 달성에 대한 STOMP 통신
                messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                    AchievementMessage.builder()
                        .userId(event.getUserId())
                        .achievementId(achievementId)
                        .progressCount(achievement.getRequirementValue())
                        .requirementValue(achievement.getRequirementValue())
                        .completed(Boolean.TRUE)
                        .build()
                );

            } else {

                if (!isAvailableToAchieveOnce(event.getActivityType())) {

                    // 업적 진행에 대한 STOMP 통신
                    messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                        AchievementMessage.builder()
                            .userId(event.getUserId())
                            .achievementId(achievementId)
                            .progressCount((Integer) progressCount)
                            .requirementValue(achievement.getRequirementValue())
                            .build()
                    );
                }
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

    private boolean isAvailableToAchieveOnce(ActivityType activityType) {
        return getProgressLevelSize(activityType).equals(MIN_PROGRESS_SIZE);
    }

    private Integer getProgressLevelSize(ActivityType activityType) {
        return achievementRepository.findMaxProgressLevelByActivityType(activityType);
    }

}
