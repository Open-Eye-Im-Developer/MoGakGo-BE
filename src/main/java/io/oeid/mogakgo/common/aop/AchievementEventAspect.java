package io.oeid.mogakgo.common.aop;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_EVENT_LISTENER_REQUEST;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_JOIN_REQUEST_NOT_FOUND;

import io.oeid.mogakgo.common.event.exception.EventListenerProcessingException;
import io.oeid.mogakgo.domain.achievement.application.AchievementEventService;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.review.application.dto.req.ReviewCreateReq;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventAspect {

    private final AchievementEventService achievementEventService;
    private final UserCommonService userCommonService;
    private final ProjectJpaRepository projectRepository;
    private final ProjectJoinRequestJpaRepository projectJoinRequestRepository;
    private final MatchingService matchingService;
    private final AchievementFacadeService achievementFacadeService;

    @Pointcut("execution(public * io.oeid.mogakgo.domain.review.application.ReviewService.createNewReview(Long, ..))")
    public void updateJandiRateExecution() {}

    @Pointcut("execution(public * io.oeid.mogakgo.domain.project.application.ProjectService.create(Long, ..))")
    public void createProjectExecution() {}

    @Pointcut("execution(public * io.oeid.mogakgo.domain.project_join_req.application.ProjectJoinRequestService.create(Long, ..))")
    public void createJoinRequestExecution() {}

    @Pointcut("execution(public * io.oeid.mogakgo.domain.project_join_req.application.ProjectJoinRequestService.accept(Long, ..))")
    public void acceptJoinRequestExecution() {}

    @Pointcut("execution(public * io.oeid.mogakgo.domain.profile.application.ProfileCardLikeService.create(Long, ..))")
    public void createLikeExecution() {}

    @Retryable(retryFor = {EventListenerProcessingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "updateJandiRateExecution() && args(userId, request)")
    public void publishCompletedEvent(JoinPoint joinPoint, Long userId, ReviewCreateReq request) {

        // -- '리뷰' 잔디력이 업데이트되는 사용자에 대한 업적 이벤트 발행
        User user = userCommonService.getUserById(userId);
        achievementEventService.publishCompletedEventWithVerify(userId,
            ActivityType.FRESH_DEVELOPER, user.getJandiRate());
    }

    @Retryable(retryFor = {EventListenerProcessingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createProjectExecution() && args(userId, request)")
    public void publishSequenceEvent(JoinPoint joinPoint, Long userId, ProjectCreateReq request) {

        try {

            // -- '생성자' 프로젝트를 생성한 사용자에 대한 업적 이벤트 발행
            achievementEventService.publishSequenceEventWithVerify(userId,
                ActivityType.PLEASE_GIVE_ME_MOGAK);

            achievementEventService.publishCompletedEventWithVerify(userId,
                ActivityType.BRAVE_EXPLORER, projectRepository.getRegionCountByUserId(userId));

        } catch (ExecutionException | InterruptedException e) {
            throw new EventListenerProcessingException(INVALID_EVENT_LISTENER_REQUEST);
        }
    }

    @Retryable(retryFor = {EventListenerProcessingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createJoinRequestExecution() && args(userId, request)")
    public void publishAccumulateEvent(JoinPoint joinPoint, Long userId, ProjectJoinCreateReq request) {

        // -- '생성자' 매칭 요청을 수신한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishAccumulateEventWithVerify(
            userId, ActivityType.CATCH_ME_IF_YOU_CAN,
            getAccumulatedProgressCount(userId, ActivityType.CATCH_ME_IF_YOU_CAN)
        );
    }

    @Retryable(retryFor = {EventListenerProcessingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "acceptJoinRequestExecution() && args(userId, projectRequestId)")
    public void publishEventAboutMatching(JoinPoint joinPoint, Long userId, Long projectRequestId) {

        try {

            // -- '생성자' 매칭 요청을 수락한 사용자에 대한 업적 이벤트 발행
            achievementEventService.publishAccumulateEventWithVerify(userId,
                ActivityType.GOOD_PERSON_GOOD_MEETUP,
                getAccumulatedProgressCount(userId, ActivityType.GOOD_PERSON_GOOD_MEETUP)
            );

            achievementEventService.publishSequenceEventWithVerify(userId, ActivityType.LIKE_E);

            achievementEventService.publishCompletedEventWithVerify(userId,
                ActivityType.NOMAD_CODER, matchingService.getRegionCountByMatching(userId));

            Long participantId = getParticipantIdFromJoinRequest(projectRequestId);
            achievementEventService.publishCompletedEventWithVerify(userId,
                ActivityType.MY_DESTINY,
                matchingService.getDuplicateMatching(userId, participantId));

            // -- '참여자' 매칭 요청을 생성한 사용자에 대한 업적 이벤트 발행
            achievementEventService.publishAccumulateEventWithVerify(participantId,
                ActivityType.GOOD_PERSON_GOOD_MEETUP,
                getAccumulatedProgressCount(participantId, ActivityType.GOOD_PERSON_GOOD_MEETUP)
            );

            achievementEventService.publishSequenceEventWithVerify(participantId,
                ActivityType.LIKE_E);

            achievementEventService.publishCompletedEventWithVerify(participantId,
                ActivityType.NOMAD_CODER, matchingService.getRegionCountByMatching(participantId));

            achievementEventService.publishCompletedEventWithVerify(participantId,
                ActivityType.MY_DESTINY,
                matchingService.getDuplicateMatching(participantId, userId));

        } catch (ExecutionException | InterruptedException e) {
            throw new EventListenerProcessingException(INVALID_EVENT_LISTENER_REQUEST);
        }
    }

    @Retryable(retryFor = {EventListenerProcessingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createLikeExecution() && args(userId, request)")
    public void publishEventAboutLike(JoinPoint joinPoint, Long userId, UserProfileLikeCreateAPIReq request) {

        // -- '찔러보기' 요청을 생성한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishCompletedEventWithoutVerify(userId, ActivityType.LEAVE_YOUR_MARK);

        // -- '찔러보기' 요청을 수신한 사용자에 대한 업적 이벤트 발행
        Long receiverId = request.getReceiverId();
        achievementEventService.publishAccumulateEventWithVerify(
            receiverId, ActivityType.WHAT_A_POPULAR_PERSON,
            achievementFacadeService.getAccumulatedProgressCount(receiverId, ActivityType.WHAT_A_POPULAR_PERSON)
        );
    }

    private Integer getAccumulatedProgressCount(Long userId, ActivityType activityType) {
        return achievementFacadeService.getAccumulatedProgressCount(userId, activityType);
    }

    private Long getParticipantIdFromJoinRequest(Long projectRequestId) {
        return projectJoinRequestRepository.findByIdWithProject(projectRequestId)
            .orElseThrow(() -> new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_NOT_FOUND))
            .getSender().getId();
    }
}
