package io.oeid.mogakgo.common.event.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;
import static io.oeid.mogakgo.exception.code.ErrorCode404.ACHIEVEMENT_NOT_FOUND;

import io.oeid.mogakgo.common.event.AccumulateAchievementEvent;
import io.oeid.mogakgo.common.event.AccumulateAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.AchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.UserActivityEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementSocketService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.AchievementMessage;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventHandler {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementJpaRepository achievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final UserCommonService userCommonService;
    private final AchievementSocketService achievementSocketService;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeActivity(final UserActivityEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(), Thread.currentThread().getName());

        User user = userCommonService.getUserById(event.getUserId());
        userActivityRepository.save(UserActivity.builder()
            .user(user)
            .activityType(event.getActivityType())
            .build());

        Achievement achievement = getById(event.getAchievementId());
        if (!isPossibleToAchieve(achievement, event.getProgressCount())) {

            log.info("call socket for event {} in progress", event.getAchievementId());

            achievementSocketService.sendMessageAboutAchievmentCompletion(
                event.getUserId(), AchievementMessage.builder()
                    .userId(event.getUserId())
                    .achievementId(event.getAchievementId())
                    .progressCount(event.getProgressCount())
                    .build()
            );

            log.info("call completed for socket event");
        }
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final SequenceAchievementEvent event) {
        User user = userCommonService.getUserById(event.getUserId());
        Achievement achievement = getById(event.getAchievementId());

        userAchievementRepository.save(
            UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .completed(event.getCompleted())
                .build()
        );
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AccumulateAchievementEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(), Thread.currentThread().getName());

        User user = userCommonService.getUserById(event.getUserId());
        Achievement achievement = getById(event.getAchievementId());

        userAchievementRepository.save(
            UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .completed(event.getCompleted())
                .build()
        );

        // 업적 진행 or 달성 후, 클라이언트에게 socket 통신
        if (event.getCompleted().equals(Boolean.TRUE)) {

            log.info("call socket for event {} completion", event.getAchievementId());

            achievementSocketService.sendMessageAboutAchievmentCompletion(
                event.getUserId(), AchievementMessage.builder()
                    .userId(event.getUserId())
                    .achievementId(event.getAchievementId())
                    .progressCount(event.getProgressCount())
                    .completed(event.getCompleted())
                    .build()
            );
        }
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AccumulateAchievementUpdateEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(), Thread.currentThread().getName());

        // 진행중인 업적에 대해 '달성' 업데이트
        UserAchievement userAchievement = getByUserAndAchievementId(event);
        userAchievement.updateCompleted();

        log.info("call socket for event {} completion", event.getAchievementId());

        // 업적 달성 후, 클라이언트에게 socket 통신
        achievementSocketService.sendMessageAboutAchievmentCompletion(
            event.getUserId(), AchievementMessage.builder()
                .userId(event.getUserId())
                .achievementId(event.getAchievementId())
                .completed(Boolean.TRUE)
                .build()
        );
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final SequenceAchievementUpdateEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(), Thread.currentThread().getName());

        // 진행중인 업적에 대해 '달성' 업데이트
        UserAchievement userAchievement = getByUserAndAchievementId(event);
        userAchievement.updateCompleted();

        // -- 'SEQUENCE' 타입 업적에 한해, 달성 조건을 위해 사용된 히스토리 soft delete 처리
        Achievement achievement = getById(event.getAchievementId());
        List<UserActivity> history = userActivityRepository.getActivityHistoryByActivityType(
            event.getUserId(), achievement.getActivityType(), achievement.getRequirementValue());
        history.forEach(UserActivity::delete);

        log.info("call socket for event {} completion", event.getAchievementId());

        // 업적 달성 후, 클라이언트에게 socket 통신
        achievementSocketService.sendMessageAboutAchievmentCompletion(
            event.getUserId(), AchievementMessage.builder()
                .userId(event.getUserId())
                .achievementId(event.getAchievementId())
                .completed(Boolean.TRUE)
                .build()
        );

        log.info("call socket completion");
    }

    private boolean isPossibleToAchieve(Achievement achievement, Integer progressCount) {
        return achievement.getRequirementValue().equals(progressCount)
            || achievement.getProgressLevel().equals(1);
    }

    public Achievement getById(Long achievementId) {
        return achievementRepository.findById(achievementId)
            .orElseThrow(() -> new AchievementException(ACHIEVEMENT_NOT_FOUND));
    }

    public UserAchievement getByUserAndAchievementId(final AchievementEvent event) {
        return userAchievementRepository
            .findByUserAndAchievementId(event.getUserId(), event.getAchievementId())
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
    }

}
