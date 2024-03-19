package io.oeid.mogakgo.domain.achievement.application;

import io.oeid.mogakgo.common.event.AccumulateAchievementEvent;
import io.oeid.mogakgo.common.event.AccumulateAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.UserActivityEvent;
import io.oeid.mogakgo.common.event.exception.EventListenerProcessingException;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AchievementEventService {

    private final AchievementService achievementService;
    private final AchievementFacadeService achievementFacadeService;
    private final ApplicationEventPublisher eventPublisher;

    // 달성 자격요건의 검증 없이 한 번에 달성 가능한 업적에 대한 이벤트 발행
    @Async
    @Retryable(retryFor = EventListenerProcessingException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public void publishCompletedEventWithoutVerify(Long userId, ActivityType activityType) {

        Long achievementId = achievementFacadeService.getAvailableAchievementId(userId, activityType);

        if (achievementId != null) {

            try {

                // 업적 이벤트 발행 - UserActivity
                eventPublisher.publishEvent(
                    UserActivityEvent.builder()
                        .userId(userId)
                        .activityType(activityType)
                        .build()
                );

                // 업적 달성 이벤트 발행 - UserAchievement
                eventPublisher.publishEvent(
                    AccumulateAchievementEvent.builder()
                        .userId(userId)
                        .achievementId(achievementId)
                        .completed(Boolean.TRUE)
                        .build()
                );

            } catch (RuntimeException e) {
                throw new EventListenerProcessingException(e.getMessage());
            }
        }
    }

    // 달성 자격요건의 검증과 함께 한 번에 달성 가능한 업적에 대한 이벤트 발행
    @Async
    @Retryable(retryFor = EventListenerProcessingException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public void publishCompletedEventWithVerify(Long userId, ActivityType activityType,
        Object target) {

        log.info("call event of {} on Thread:{}", activityType, Thread.currentThread().getName());

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 없적이 없다면 이벤트 발행 X --
        Long achievementId = achievementFacadeService.getAvailableAchievementId(userId, activityType);

        if (achievementId != null) {

            // 사용자가 업적 달성 조건을 만족했을 경우
            // -- 사용자의 잔디력이 requirementValue 이상을 달성했다면 이벤트 발행 O
            if (validateAvailabilityToAchieve(target, achievementId)) {

                try {

                    // 업적 히스토리 이벤트 발행 - UserActivity
                    eventPublisher.publishEvent(
                        UserActivityEvent.builder()
                            .userId(userId)
                            .activityType(activityType)
                            .build()
                    );

                    // 업적 달성 이벤트 발행 - UserAchievement
                    eventPublisher.publishEvent(
                        AccumulateAchievementEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .completed(Boolean.TRUE)
                            .build()
                    );

                } catch (RuntimeException e) {
                    throw new EventListenerProcessingException(e.getMessage());
                }
            }
        }
    }

    // 달성 자격요건의 검증과 함께 여러 번에 걸쳐 달성 가능한 업적에 대한 이벤트 발행
    @Async
    @Retryable(retryFor = EventListenerProcessingException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public void publishAccumulateEventWithVerify(Long userId, ActivityType activityType,
        Integer progressCount) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 업적이 없는 경우 이벤트 발행 X
        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(userId, activityType);

        if (achievementId != null) {

            try {

                // 업적에 대한 이벤트 발행 - UserActivity
                // -- 누적 횟수 이벤트
                eventPublisher.publishEvent(
                    UserActivityEvent.builder()
                        .userId(userId)
                        .activityType(activityType)
                        .build()
                );

                Boolean isExist = achievementFacadeService
                    .validateAchivementAlreadyInProgress(userId, achievementId);

                if (isExist.equals(Boolean.FALSE)) {
                    eventPublisher.publishEvent(
                        AccumulateAchievementEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .progressCount(progressCount)
                            .completed(Boolean.FALSE)
                            .build()
                    );
                }

                // 해당 업적에 대한 달성 조건을 만족했을 경우
                if (validateAvailabilityToAchieve(progressCount + 1, achievementId)) {

                    // 업적 달성에 대한 이벤트 발행 - UserAchievement
                    eventPublisher.publishEvent(
                        AccumulateAchievementUpdateEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .build()
                    );
                }

            } catch (RuntimeException e) {
                throw new EventListenerProcessingException(e.getMessage());
            }
        }
    }

    // 달성 자격요건의 검증과 함께 여러 번에 걸쳐 달성 가능한 연속성 업적에 대한 이벤트 발행
    @Async
    @Retryable(retryFor = EventListenerProcessingException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public void publishSequenceEventWithVerify(Long userId, ActivityType activityType) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 업적이 없는 경우 이벤트 발행 X
        Long achievementId = achievementFacadeService
            .getAvailableAchievementId(userId, activityType);

        // 오늘을 제외한, 업적의 진행도 조회
        Map<ActivityType, Integer> map = achievementService
            .getProgressCountMap(userId, List.of(activityType));

        if (achievementId != null) {

            try {

                // 업적에 대한 이벤트 발행 - UserActivity
                // -- 연속 횟수 이벤트
                if (achievementFacadeService.validateActivityDuplicate(userId, activityType)) {
                    eventPublisher.publishEvent(
                        UserActivityEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .activityType(activityType)
                            .progressCount(map.get(activityType) + 1)
                            .build()
                    );
                }

                Boolean isExist = achievementFacadeService
                    .validateAchivementAlreadyInProgress(userId, achievementId);

                // 업적 진행에 대한 이벤트 발행 - UserAchievement
                if (isExist.equals(Boolean.FALSE)) {
                    eventPublisher.publishEvent(
                        AccumulateAchievementEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .completed(Boolean.FALSE)
                            .build()
                    );
                }

                // 해당 업적에 대한 달성 조건을 만족했을 경우
                if (validateAvailabilityToAchieve(map.get(activityType) + 1, achievementId)) {

                    // 업적 달성에 대한 이벤트 발행 - UserAchievement
                    eventPublisher.publishEvent(
                        SequenceAchievementUpdateEvent.builder()
                            .userId(userId)
                            .achievementId(achievementId)
                            .build()
                    );
                }
            } catch (RuntimeException e) {
                throw new EventListenerProcessingException(e.getMessage());
            }
        }
    }

    @Recover
    public void recoverForEventListenerProcess(
        EventListenerProcessingException e, Long userId, ActivityType activityType) {
        throw new AchievementException(ErrorCode400.EVENT_LISTENER_REQUEST_FAILED);
    }

    private boolean validateAvailabilityToAchieve(Object target, Long achievementId) {
        Achievement achievement = achievementFacadeService.getById(achievementId);
        if (target instanceof Integer) {
            return Objects.equals(achievement.getRequirementValue(), target);
        } else if (target instanceof Double) {
            return achievement.getRequirementValue() <= (Double) target;
        } else {
            throw new IllegalArgumentException("Unsupported target type");
        }
    }
}
