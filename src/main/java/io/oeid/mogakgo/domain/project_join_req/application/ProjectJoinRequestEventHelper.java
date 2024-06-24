package io.oeid.mogakgo.domain.project_join_req.application;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
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
public class ProjectJoinRequestEventHelper {

    private final OutboxJpaRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Long userId) {

        // -- '생성자' 매칭 요청을 수신한 사용자에 대한 업적 이벤트 발행
        registerEvent(userId, ActivityType.CATCH_ME_IF_YOU_CAN, null);
    }

    @Transactional
    public void registerEvent(Long userId, ActivityType activityType, Object target) {

        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.ACHIEVEMENT)
            .key(generateKey(userId, activityType))
            .target(setTarget(target))
            .build()
        );

        publishEvent(userId, activityType, target);
    }

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

    private Integer setTarget(Object target) {
        return target != null ? (Integer) target : null;
    }

}
