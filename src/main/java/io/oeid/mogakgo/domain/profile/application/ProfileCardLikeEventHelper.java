package io.oeid.mogakgo.domain.profile.application;

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
@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
@RequiredArgsConstructor
public class ProfileCardLikeEventHelper {

    private final OutboxJpaRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Long userId, Long receiverId) {

        // -- '찔러보기' 요청을 생성한 사용자에 대한 업적 이벤트 발행
        publishEvent(userId, ActivityType.LEAVE_YOUR_MARK, null);

        // -- '찔러보기' 요청을 수신한 사용자에 대한 업적 이벤트 발행
        publishEvent(receiverId, ActivityType.WHAT_A_POPULAR_PERSON, null);
    }

    public void publishEvent(Long userId, ActivityType activityType, Object target) {

        // -- 업적 이력 및 달성 처리에 대한 이벤트 발행
        eventPublisher.publishEvent(AchievementEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .target(target)
            .build()
        );

        outboxRepository.save(OutboxEvent.builder()
            .type(EventType.ACHIEVEMENT)
            .key(generateKey(userId, activityType))
            .build()
        );
    }

    private String generateKey(Long userId, ActivityType activityType) {
        return userId.toString() + activityType.toString();
    }

}
