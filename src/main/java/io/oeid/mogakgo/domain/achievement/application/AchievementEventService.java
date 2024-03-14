package io.oeid.mogakgo.domain.achievement.application;

import io.oeid.mogakgo.common.event.AccumulateAchievementEvent;
import io.oeid.mogakgo.common.event.AccumulateAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.UserActivityEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventService {

    private final AchievementService achievementService;
    private final AchievementFacadeService achievementFacadeService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishCompletedEventWithoutVerify(Long userId, ActivityType activityType) {

        Long achievementId = achievementFacadeService.findAvailableAchievement(userId, activityType);

        if (achievementId != null) {

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
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishSequenceEventWithVerify(Long userId, ActivityType activityType) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 업적이 없는 경우 이벤트 발행 X
        Long achievementId = achievementFacadeService
            .findAvailableAchievement(userId, activityType);

        // 오늘을 제외한, 업적의 진행도 조회
        Map<ActivityType, Integer> map = achievementService
            .getProgressCountMap(userId, List.of(activityType));

        if (achievementId != null) {

            // 업적에 대한 이벤트 발행 - UserActivity
            // -- 연속 횟수 이벤트
            if (achievementFacadeService.validateActivityDuplicate(userId, activityType)) {
                eventPublisher.publishEvent(
                    UserActivityEvent.builder()
                        .userId(userId)
                        .activityType(activityType)
                        .build()
                );
            }

            Boolean isExist = achievementFacadeService
                .getUserAchievementByAchievementId(userId, achievementId);

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
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishCompletedEventWithVerify(Long userId, ActivityType activityType,
        Object target) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 없적이 없다면 이벤트 발행 X --
        Long achievementId = achievementFacadeService.findAvailableAchievement(userId, activityType);

        if (achievementId != null) {

            // 사용자가 업적 달성 조건을 만족했을 경우
            // -- 사용자의 잔디력이 requirementValue 이상을 달성했다면 이벤트 발행 O
            if (validateAvailabilityToAchieve(target, achievementId)) {

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
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishAccumulateEventWithVerify(Long userId, ActivityType activityType,
        Integer progressCount) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        // -- 현재 사용자가 달성할 수 있는 업적이 없는 경우 이벤트 발행 X
        Long achievementId = achievementFacadeService
            .findAvailableAchievement(userId, activityType);

        if (achievementId != null) {

            // 업적에 대한 이벤트 발행 - UserActivity
            // -- 누적 횟수 이벤트
            eventPublisher.publishEvent(
                UserActivityEvent.builder()
                    .userId(userId)
                    .activityType(activityType)
                    .build()
            );

            Boolean isExist = achievementFacadeService
                .getUserAchievementByAchievementId(userId, achievementId);

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
            if (validateAvailabilityToAchieve(progressCount, achievementId)) {

                // 업적 달성에 대한 이벤트 발행 - UserAchievement
                eventPublisher.publishEvent(
                    AccumulateAchievementUpdateEvent.builder()
                        .userId(userId)
                        .achievementId(achievementId)
                        .build()
                );
            }
        }
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
