package io.oeid.mogakgo.domain.matching.application;

import io.oeid.mogakgo.common.event.domain.vo.AchievementCompletionEvent;
import io.oeid.mogakgo.common.event.domain.vo.AchievementNotificationEvent;
import io.oeid.mogakgo.common.event.domain.vo.UserActivityEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
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

    @Async("threadPoolTaskExecutor")
    @Transactional
    public void publishEvent(Long userId, ActivityType activityType, Object target) {

        // -- 업적 이력에 대한 이벤트 발행
        eventPublisher.publishEvent(UserActivityEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .build()
        );

        // -- 업적 달성 검증 및 처리에 대한 이벤트 발행
        eventPublisher.publishEvent(AchievementCompletionEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .target(target)
            .build()
        );

        // -- 업적 달성 알림에 대한 이벤트 발행
        eventPublisher.publishEvent(AchievementNotificationEvent.builder()
            .userId(userId)
            .activityType(activityType)
            .target(target)
            .build()
        );
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
