package io.oeid.mogakgo.common.aop;

import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_JOIN_REQUEST_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;

import io.oeid.mogakgo.domain.achievement.application.AchievementEventService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.review.application.dto.req.ReviewCreateReq;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventAspect {

    private static final int MAX_SERVICE_AREA = 26;
    private static final int SAME_MATCHING_COUNT = 2;

    private final AchievementEventService achievementEventService;
    private final UserCommonService userCommonService;
    private final ProjectJpaRepository projectRepository;
    private final ProjectJoinRequestJpaRepository projectJoinRequestRepository;
    private final MatchingService matchingService;
    private final AchievementProgressService achievementProgressService;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "updateJandiRateExecution() && args(userId, request)")
    public void publishCompletedEvent(JoinPoint joinPoint, Long userId, ReviewCreateReq request) {

        // -- '리뷰' 잔디력이 업데이트되는 사용자에 대한 업적 이벤트 발행
        User receiver = userCommonService.getUserById(request.getReceiverId());
        achievementEventService.publishCompletedEventWithVerify(receiver.getId(),
            ActivityType.FRESH_DEVELOPER, receiver.getJandiRate());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createProjectExecution() && args(userId, request)")
    public void publishSequenceEvent(JoinPoint joinPoint, Long userId, ProjectCreateReq request) {

        log.info("AOP 호출 on Thread={}", Thread.currentThread().getName());

        // -- '생성자' 프로젝트를 생성한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishSequenceEventWithVerify(userId,
            ActivityType.PLEASE_GIVE_ME_MOGAK);

        achievementEventService.publishCompletedEventWithVerify(userId,
            ActivityType.BRAVE_EXPLORER, checkCreatedProjectCountByRegion(userId));

        log.info("AOP 호출 완료 on Thread={}", Thread.currentThread().getName());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createJoinRequestExecution() && args(userId, request)")
    public void publishAccumulateEvent(JoinPoint joinPoint, Long userId, ProjectJoinCreateReq request) {

        // -- '생성자' 매칭 요청을 수신한 사용자에 대한 업적 이벤트 발행
        Project project = getProject(request.getProjectId());
        achievementEventService.publishAccumulateEventWithVerify(
            userId, ActivityType.CATCH_ME_IF_YOU_CAN,
            getAccumulatedProgressCount(project.getCreator().getId(), ActivityType.CATCH_ME_IF_YOU_CAN)
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "acceptJoinRequestExecution() && args(userId, projectRequestId)")
    public void publishEventAboutMatching(JoinPoint joinPoint, Long userId, Long projectRequestId) {

        // -- '생성자' 매칭 요청을 수락한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishAccumulateEventWithVerify(userId,
            ActivityType.GOOD_PERSON_GOOD_MEETUP,
            getAccumulatedProgressCount(userId, ActivityType.GOOD_PERSON_GOOD_MEETUP)
        );

        achievementEventService.publishSequenceEventWithVerify(userId, ActivityType.LIKE_E);

        achievementEventService.publishCompletedEventWithVerify(userId,
            ActivityType.NOMAD_CODER, checkMatchedProjectCountByRegion(userId));

        Long participantId = getParticipantIdFromJoinRequest(projectRequestId);
        achievementEventService.publishCompletedEventWithVerify(userId,
            ActivityType.MY_DESTINY,
            checkMatchingCountWithSameUser(userId, participantId));

        // -- '참여자' 매칭 요청을 생성한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishAccumulateEventWithVerify(participantId,
            ActivityType.GOOD_PERSON_GOOD_MEETUP,
            getAccumulatedProgressCount(participantId, ActivityType.GOOD_PERSON_GOOD_MEETUP)
        );

        achievementEventService.publishSequenceEventWithVerify(participantId,
            ActivityType.LIKE_E);

        achievementEventService.publishCompletedEventWithVerify(participantId,
            ActivityType.NOMAD_CODER, checkMatchedProjectCountByRegion(participantId));

        achievementEventService.publishCompletedEventWithVerify(participantId,
            ActivityType.MY_DESTINY,
            checkMatchingCountWithSameUser(userId, participantId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterReturning(pointcut = "createLikeExecution() && args(userId, request)")
    public void publishEventAboutLike(JoinPoint joinPoint, Long userId, UserProfileLikeCreateAPIReq request) {

        // -- '찔러보기' 요청을 생성한 사용자에 대한 업적 이벤트 발행
        achievementEventService.publishCompletedEventWithoutVerify(userId, ActivityType.LEAVE_YOUR_MARK);

        // -- '찔러보기' 요청을 수신한 사용자에 대한 업적 이벤트 발행
        Long receiverId = request.getReceiverId();
        achievementEventService.publishAccumulateEventWithVerify(
            receiverId, ActivityType.WHAT_A_POPULAR_PERSON,
            getAccumulatedProgressCount(receiverId, ActivityType.WHAT_A_POPULAR_PERSON)
        );
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(PROJECT_NOT_FOUND));
    }

    private Integer checkCreatedProjectCountByRegion(Long userId) {
        Integer progressCount = projectRepository.getRegionCountByUserId(userId);
        return progressCount.equals(MAX_SERVICE_AREA) ? 1 : 0;
    }

    private Integer checkMatchedProjectCountByRegion(Long userId) {
        Integer progressCount = matchingService.getRegionCountByMatching(userId);
        return progressCount.equals(MAX_SERVICE_AREA) ? 1 : 0;
    }

    private Integer checkMatchingCountWithSameUser(Long userId, Long participantId) {
        Integer progressCount = matchingService.getDuplicateMatching(userId, participantId);
        return progressCount.equals(SAME_MATCHING_COUNT) ? 1 : 0;
    }

    private Integer getAccumulatedProgressCount(Long userId, ActivityType activityType) {
        return achievementProgressService.getAccumulatedProgressCount(userId, activityType);
    }

    private Long getParticipantIdFromJoinRequest(Long projectRequestId) {
        return projectJoinRequestRepository.findByIdWithProject(projectRequestId)
            .orElseThrow(() -> new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_NOT_FOUND))
            .getSender().getId();
    }
}
