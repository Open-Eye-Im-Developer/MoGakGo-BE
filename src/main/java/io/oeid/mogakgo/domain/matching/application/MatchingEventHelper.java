package io.oeid.mogakgo.domain.matching.application;

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
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class MatchingEventHelper {

    private static final int MAX_SERVICE_AREA = 26;
    private static final int SAME_MATCHING_COUNT = 2;

    private final OutboxJpaRepository outboxRepository;
    private final MatchingService matchingService;
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Long userId, Long participantId) {

        // -- '생성자' 매칭 요청을 수락한 사용자에 대한 업적 이벤트 발행
        publishEvent(userId, ActivityType.GOOD_PERSON_GOOD_MEETUP, null);
        publishEvent(userId, ActivityType.LIKE_E, null);
        publishEvent(userId, ActivityType.NOMAD_CODER, checkMatchedProjectCountByRegion(userId));
        publishEvent(userId, ActivityType.MY_DESTINY, checkMatchingCountWithSameUser(userId, participantId));

        // -- '참여자' 매칭 요청을 생성한 사용자에 대한 업적 이벤트 발행
        publishEvent(participantId, ActivityType.GOOD_PERSON_GOOD_MEETUP, null);
        publishEvent(participantId, ActivityType.LIKE_E, null);
        publishEvent(participantId, ActivityType.NOMAD_CODER, checkMatchedProjectCountByRegion(userId));
        publishEvent(participantId, ActivityType.MY_DESTINY, checkMatchingCountWithSameUser(userId, participantId));
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

    private Integer checkMatchedProjectCountByRegion(Long userId) {
        Integer progressCount = matchingService.getRegionCountByMatching(userId);
        return progressCount.equals(MAX_SERVICE_AREA) ? 1 : 0;
    }

    private Integer checkMatchingCountWithSameUser(Long userId, Long participantId) {
        Integer progressCount = matchingService.getDuplicateMatching(userId, participantId);
        return progressCount.equals(SAME_MATCHING_COUNT) ? 1 : 0;
    }

}
