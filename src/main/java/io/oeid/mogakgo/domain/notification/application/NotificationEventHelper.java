package io.oeid.mogakgo.domain.notification.application;


import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
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
public class NotificationEventHelper {

    private final ApplicationEventPublisher eventPublisher;
    private final OutboxJpaRepository outboxRepository;

    public void publishEvent(final AchievementEvent event) {

        registerEvent(event.getUserId(), event.getActivityType());
    }

    private void registerEvent(Long userId, ActivityType activityType) {

        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.NOTIFICATION)
            .key(generateKey(userId, activityType))
            .build()
        );

        publishEvent(userId, activityType);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(Long userId, ActivityType activityType) {

        eventPublisher.publishEvent(NotificationEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .build()
        );
    }

    private String generateKey(Long userId, ActivityType activityType) {
        return userId.toString() + ":" + activityType.toString();
    }

}
