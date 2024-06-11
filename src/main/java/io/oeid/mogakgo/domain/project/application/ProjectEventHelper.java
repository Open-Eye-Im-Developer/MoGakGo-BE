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
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class ProjectEventHelper {

    private static final int MAX_SERVICE_AREA = 26;

    private final OutboxJpaRepository outboxRepository;
    private final ProjectJpaRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(Long userId) {

        // -- '생성자' 프로젝트를 생성한 사용자에 대한 업적 이벤트 발행
        // TODO: 발행해야 하는 여러 이벤트에 대해 병렬적으로 처리할 수 있을지 고민
        publishEvent(userId, ActivityType.PLEASE_GIVE_ME_MOGAK, null);
        publishEvent(userId, ActivityType.BRAVE_EXPLORER, checkCreatedProjectCountByRegion(userId));
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

        // TODO: Outbox를 식별할 수 있는 식별자를 어떻게 구성할지 고민
        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.ACHIEVEMENT)
            .key(generateKey(userId, activityType))
            .build()
        );
    }

    private Integer checkCreatedProjectCountByRegion(Long userId) {
        Integer progressCount = projectRepository.getRegionCountByUserId(userId);
        return progressCount.equals(MAX_SERVICE_AREA) ? 1 : 0;
    }

    // TODO: key값을 중복이 없는, 온전히 고유한 값으로 만들기 위한 요소가 필요
    private String generateKey(Long userId, ActivityType activityType) {
        return userId.toString() + activityType.toString();
    }

}
