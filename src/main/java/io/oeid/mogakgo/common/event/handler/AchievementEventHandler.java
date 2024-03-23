package io.oeid.mogakgo.common.event.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode400.EVENT_LISTENER_REQUEST_FAILED;
import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;
import static io.oeid.mogakgo.exception.code.ErrorCode404.ACHIEVEMENT_NOT_FOUND;

import io.oeid.mogakgo.common.event.AccumulateAchievementEvent;
import io.oeid.mogakgo.common.event.AccumulateAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.AchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.UserActivityEvent;
import io.oeid.mogakgo.common.event.exception.EventListenerProcessingException;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.AchievementMessage;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventHandler {

    private static final String SUBSCRIBE_DESTINATIONN = "/topic/achievement/";

    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementJpaRepository achievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final UserCommonService userCommonService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Retryable(retryFor = EventListenerProcessingException.class, backoff = @Backoff(1000))
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeActivity(final UserActivityEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(),
            Thread.currentThread().getName());

        try {

            User user = userCommonService.getUserById(event.getUserId());
            userActivityRepository.save(UserActivity.builder()
                .user(user)
                .activityType(event.getActivityType())
                .build());

            Achievement achievement = getById(event.getAchievementId());
            if (!achievement.getRequirementValue().equals(event.getProgressCount())
                || !getProgressLevelSize(event.getActivityType()).equals(1)) {

                log.info("call socket for event {} in progress", event.getAchievementId());

                messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                    AchievementMessage.builder()
                        .userId(event.getUserId())
                        .achievementId(event.getAchievementId())
                        .progressCount(event.getProgressCount())
                        .requirementValue(achievement.getRequirementValue())
                        .build()
                );

                log.info("call completed for socket event");
            }

        } catch (RuntimeException e) {
            throw new EventListenerProcessingException(e.getMessage());
        }
    }

    @Retryable(retryFor = EventListenerProcessingException.class, backoff = @Backoff(1000))
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final SequenceAchievementEvent event) {

        try {
            User user = userCommonService.getUserById(event.getUserId());
            Achievement achievement = getById(event.getAchievementId());

            userAchievementRepository.save(
                UserAchievement.builder()
                    .user(user)
                    .achievement(achievement)
                    .completed(event.getCompleted())
                    .build()
            );
        } catch (RuntimeException e) {
            throw new EventListenerProcessingException(e.getMessage());
        }
    }

    @Retryable(retryFor = EventListenerProcessingException.class, maxAttempts = 3, backoff = @Backoff(1000))
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AccumulateAchievementEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(),
            Thread.currentThread().getName());

        try {
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
                notificationService.createAchievementNotification(user.getId(), achievement);

                messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                    AchievementMessage.builder()
                        .userId(event.getUserId())
                        .achievementId(event.getAchievementId())
                        .progressCount(event.getProgressCount())
                        .requirementValue(achievement.getRequirementValue())
                        .completed(event.getCompleted())
                        .build()
                );

            }
        } catch (RuntimeException e) {
            throw new EventListenerProcessingException(e.getMessage());
        }
    }

    @Retryable(retryFor = EventListenerProcessingException.class, backoff = @Backoff(1000))
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final AccumulateAchievementUpdateEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(),
            Thread.currentThread().getName());

        try {
            // 진행중인 업적에 대해 '달성' 업데이트
            UserAchievement userAchievement = getByUserAndAchievementId(event);
            userAchievement.updateCompleted();

            log.info("call socket for event {} completion", event.getAchievementId());
            notificationService.createAchievementNotification(userAchievement.getUser().getId(), userAchievement.getAchievement());

            // 업적 달성 후, 클라이언트에게 socket 통신
            Achievement achievement = getById(event.getAchievementId());

            messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                AchievementMessage.builder()
                    .userId(event.getUserId())
                    .achievementId(event.getAchievementId())
                    .progressCount(achievement.getRequirementValue())
                    .requirementValue(achievement.getRequirementValue())
                    .completed(Boolean.TRUE)
                    .build()
            );

        } catch (RuntimeException e) {
            throw new EventListenerProcessingException(e.getMessage());
        }
    }

    @Retryable(retryFor = EventListenerProcessingException.class, backoff = @Backoff(1000))
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void executeEvent(final SequenceAchievementUpdateEvent event) {

        log.info("call activity event of {} on Thread:{}", event.getAchievementId(),
            Thread.currentThread().getName());

        try {
            // 진행중인 업적에 대해 '달성' 업데이트
            UserAchievement userAchievement = getByUserAndAchievementId(event);
            userAchievement.updateCompleted();

            // -- 'SEQUENCE' 타입 업적에 한해, 달성 조건을 위해 사용된 히스토리 soft delete 처리
            Achievement achievement = getById(event.getAchievementId());
            List<UserActivity> history = userActivityRepository.getActivityHistoryByActivityType(
                event.getUserId(), achievement.getActivityType(),
                achievement.getRequirementValue());
            history.forEach(UserActivity::delete);

            log.info("call socket for event {} completion", event.getAchievementId());
            notificationService.createAchievementNotification(userAchievement.getUser().getId(), achievement);

            // 업적 달성 후, 클라이언트에게 socket 통신
            messagingTemplate.convertAndSend(SUBSCRIBE_DESTINATIONN + event.getUserId(),
                AchievementMessage.builder()
                    .userId(event.getUserId())
                    .achievementId(event.getAchievementId())
                    .progressCount(achievement.getRequirementValue())
                    .requirementValue(achievement.getRequirementValue())
                    .completed(Boolean.TRUE)
                    .build()
            );

        } catch (RuntimeException e) {
            throw new EventListenerProcessingException(e.getMessage());
        }

        log.info("call socket completion");
    }

    @Recover
    public void recoverForActivityEvent(EventListenerProcessingException e,
        final UserActivityEvent event) {
        throw new AchievementException(EVENT_LISTENER_REQUEST_FAILED);
    }

    @Recover
    public void recoverForAccumulatedEvent(EventListenerProcessingException e,
        final AccumulateAchievementEvent event) {
        throw new AchievementException(EVENT_LISTENER_REQUEST_FAILED);
    }

    @Recover
    public void recoverForSequencedEvent(EventListenerProcessingException e,
        final SequenceAchievementEvent event) {
        throw new AchievementException(EVENT_LISTENER_REQUEST_FAILED);
    }

    @Recover
    public void recoverForAccumulatedUpdateEvent(EventListenerProcessingException e,
        final AccumulateAchievementUpdateEvent event) {
        throw new AchievementException(EVENT_LISTENER_REQUEST_FAILED);
    }

    @Recover
    public void recoverForSequencedUpdateEvent(EventListenerProcessingException e,
        final AccumulateAchievementUpdateEvent event) {
        throw new AchievementException(EVENT_LISTENER_REQUEST_FAILED);
    }

    public Achievement getById(Long achievementId) {
        return achievementRepository.findById(achievementId)
            .orElseThrow(() -> new AchievementException(ACHIEVEMENT_NOT_FOUND));
    }

    public Integer getProgressLevelSize(ActivityType activityType) {
        return achievementRepository.findByActivityType(activityType).size();
    }

    public UserAchievement getByUserAndAchievementId(final AchievementEvent event) {
        return userAchievementRepository
            .findByUserAndAchievementId(event.getUserId(), event.getAchievementId())
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
    }

}
