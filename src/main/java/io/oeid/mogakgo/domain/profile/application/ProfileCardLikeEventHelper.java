package io.oeid.mogakgo.domain.profile.application;

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
@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
@RequiredArgsConstructor
public class ProfileCardLikeEventHelper {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Long userId, Long receiverId) {

        // -- '찔러보기' 요청을 생성한 사용자에 대한 업적 이벤트 발행
        publishEvent(userId, ActivityType.LEAVE_YOUR_MARK, null);

        // -- '찔러보기' 요청을 수신한 사용자에 대한 업적 이벤트 발행
        publishEvent(receiverId, ActivityType.WHAT_A_POPULAR_PERSON, null);
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

}
