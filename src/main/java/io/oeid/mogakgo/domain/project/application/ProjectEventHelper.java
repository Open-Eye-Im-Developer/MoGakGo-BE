package io.oeid.mogakgo.domain.project.application;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
@RequiredArgsConstructor
public class ProjectEventHelper {

    private static final int MAX_SERVICE_AREA = 26;

    private final OutboxJpaRepository outboxRepository;
    private final ProjectJpaRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void publishEvent(Long userId) {

        // -- '생성자' 프로젝트를 생성한 사용자에 대한 업적 이벤트 발행
        // TODO: 발행해야 하는 여러 이벤트에 대해 병렬적으로 처리할 수 있을지 고민
        registerEvent(userId, ActivityType.PLEASE_GIVE_ME_MOGAK, null);
        registerEvent(userId, ActivityType.BRAVE_EXPLORER, checkCreatedProjectCountByRegion(userId));
    }

    @Transactional
    public void registerEvent(Long userId, ActivityType activityType, Object target) {

        log.info("eventHelper register event type of '{}' completely and publish event through thread '{}",
            activityType, Thread.currentThread().getName());

        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.ACHIEVEMENT)
            .key(generateKey(userId, activityType))
            .target(setTarget(target))
            .build()
        );

        // -- 이벤트 발행
        publishEvent(userId, activityType, target);
    }

    private void publishEvent(Long userId, ActivityType activityType, Object target) {

        // -- 업적 이력 및 달성 처리에 대한 이벤트 발행
        // TODO: 이벤트 발행 자체가 실패하더라도 도메인 로직은 롤백되어서는 안되므로 재발행 로직 구현해야 함
        eventPublisher.publishEvent(AchievementEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .target(target)
            .build()
        );
    }

    private Integer checkCreatedProjectCountByRegion(Long userId) {
        Integer progressCount = projectRepository.getRegionCountByUserId(userId);
        return progressCount.equals(MAX_SERVICE_AREA) ? 1 : 0;
    }

    // TODO: key값을 중복이 없는, 온전히 고유한 값으로 만들기 위한 요소가 필요
    private String generateKey(Long userId, ActivityType activityType) {
        return userId.toString() + ":" + activityType.toString();
    }

    private Integer setTarget(Object target) {
        return target != null ? (Integer) target : null;
    }

}
