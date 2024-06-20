package io.oeid.mogakgo.domain.review.application;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class ReviewEventHelper {

    private final OutboxJpaRepository outboxRepository;
    private final UserCommonService userCommonService;
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Long userId, Double jandiRate) {

        // -- '리뷰' 잔디력이 업데이트되는 사용자에 대한 업적 이벤트 발행
        registerEvent(userId, ActivityType.FRESH_DEVELOPER, checkUserJandiRate(userId) + jandiRate);
    }

    private void registerEvent(Long userId, ActivityType activityType, Object target) {

        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.ACHIEVEMENT)
            .key(generateKey(userId, activityType))
            .target(setTarget(target))
            .build()
        );

        publishEvent(userId, activityType, target);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(Long userId, ActivityType activityType, Object target) {

        // -- 업적 이력 및 달성 처리에 대한 이벤트 발행
        eventPublisher.publishEvent(AchievementEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .target(target)
            .build()
        );
    }

    private String generateKey(Long userId, ActivityType activityType) {
        return userId.toString() + ":" + activityType.toString();
    }

    private Double checkUserJandiRate(Long userId) {
        return userCommonService.getUserById(userId).getJandiRate();
    }

    private Integer setTarget(Object target) {
        return target != null ? (Integer) target : null;
    }

}
