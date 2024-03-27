package io.oeid.mogakgo.domain.project_join_req.application;

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
public class ProjectJoinRequestEventHelper {

    private final ApplicationEventPublisher eventPublisher;

    // -- 비동기 (@Async) 로 호출되는 메서드에 대해 트랜잭션으로 묶으면, 예외가 발생해도 롤백되지 않음!
    public void publishEvent(Long userId) {

        // -- '생성자' 매칭 요청을 수신한 사용자에 대한 업적 이벤트 발행
        publishEvent(userId, ActivityType.CATCH_ME_IF_YOU_CAN, null);
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
