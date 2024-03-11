package io.oeid.mogakgo.domain.project_join_req.application;

import static io.oeid.mogakgo.domain.notification.domain.enums.FCMNotificationType.MATCHING_SUCCEEDED;
import static io.oeid.mogakgo.domain.notification.domain.enums.NotificationMessage.MATCHING_SUCCESS_MESSAGE;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_MATCHING_USER_TO_ACCEPT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SENDER_TO_ACCEPT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_JOIN_REQUEST_SHOULD_BE_ONLY_ONE;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_JOIN_REQUEST_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.matching.application.UserMatchingService;
import io.oeid.mogakgo.domain.notification.application.FCMNotificationService;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProjectJoinRequestService {

    private final ProjectJoinRequestJpaRepository projectJoinRequestRepository;
    private final UserJpaRepository userRepository;
    private final ProjectJpaRepository projectRepository;
    private final UserMatchingService userMatchingService;
    private final MatchingService matchingService;
    private final FCMNotificationService fcmNotificationService;
    private final UserCommonService userCommonService;
    private final NotificationService notificationService;

    @Transactional
    public Long create(Long userId, ProjectJoinCreateReq request) {
        User tokenUser = validateToken(userId);

        Project project = validateProject(request.getProjectId());

        // 다른 프로젝트에 매칭 요청을 보낸 적이 있는지 검증 (매칭은 한 번에 하나만 가능하므로)
        validateAnotherRequestAlreadyExist(userId);

        // 프로젝트 매칭 요청 생성
        ProjectJoinRequest joinRequest = request.toEntity(tokenUser, project);
        projectJoinRequestRepository.save(joinRequest);

        return joinRequest.getId();
    }

    @Transactional
    public Long accept(Long userId, Long projectRequestId) {
        // 유저 존재 여부 체크
        User tokenUser = userCommonService.getUserById(userId);

        // 프로젝트 요청 존재 여부 체크
        ProjectJoinRequest projectJoinRequest = getProjectJoinRequestWithProject(projectRequestId);

        // 내가 현재 매칭이 진행 중이면 예외를 발생
        if (userMatchingService.hasProgressMatching(tokenUser.getId())) {
            throw new ProjectJoinRequestException(INVALID_MATCHING_USER_TO_ACCEPT);
        }

        // TODO: 내가 매칭이 되면 프로젝트 요청들은 다 취소 처리 된다.-> 이것때문에 필요한 로직인지 생각해보기
        // 요청 보낸 상대가 매칭 될 수 있는 상태인지 체크
        if (userMatchingService.hasProgressMatching(projectJoinRequest.getSender().getId())) {
            throw new ProjectJoinRequestException(INVALID_SENDER_TO_ACCEPT);
        }

        // ---- 같은 트랜잭션
        // 프로젝트 요청 수락
        projectJoinRequest.accept(tokenUser);
        // 매칭 생성
        Long matchingId = matchingService.create(projectJoinRequest);
        //----

        // 프로젝트에 대한 요청들 수락 되지 않은 것들 다 거절 처리
        // 비동기 가능
        projectJoinRequestRepository.rejectNoAcceptedByProjectId(
            projectJoinRequest.getProject().getId(), projectJoinRequest.getId());

        // 내가 보낸 대기 중 요청이 있으면 취소 처리
        // 여기서 나는 에러는 클라와 상관이 없으므로 에러가 발생해도 넘어갈 수 있게 처리.
        try {
            cancelMyPendingProjectJoinRequest(tokenUser);
        } catch (ProjectJoinRequestException e) {
            // TODO: 로그 처리
        }
        fcmNotificationService.sendNotification(projectJoinRequest.getSender().getId(),
            MATCHING_SUCCESS_MESSAGE.getTitle(), MATCHING_SUCCESS_MESSAGE.getMessage(),
            MATCHING_SUCCEEDED);
        notificationService.createMatchingSuccessNotification(projectJoinRequest.getSender().getId(),
            projectJoinRequest.getProject());
        return matchingId;
    }

    private void cancelMyPendingProjectJoinRequest(User sender) {
        projectJoinRequestRepository.findPendingBySenderId(sender.getId())
            .ifPresent(pendingProjectJoinRequest -> pendingProjectJoinRequest.cancel(sender));
    }

    private ProjectJoinRequest getProjectJoinRequestWithProject(Long projectRequestId) {
        return projectJoinRequestRepository.findByIdWithProject(projectRequestId)
            .orElseThrow(() -> new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_NOT_FOUND));
    }

    public CursorPaginationResult<ProjectJoinRequestDetailAPIRes> getBySenderIdWithPagination(
        Long userId, Long senderId, CursorPaginationInfoReq pageable
    ) {
        User tokenUser = validateToken(userId);
        validateSender(tokenUser, senderId);

        // 사용자가 보낸 프로젝트 매칭 요청 리스트
        return projectJoinRequestRepository.getBySenderIdWithPagination(
            senderId, null, null, pageable
        );
    }

    private User validateToken(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    private void validateSender(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION);
        }
    }

    private Project validateProject(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(PROJECT_NOT_FOUND));
    }

    private void validateAnotherRequestAlreadyExist(Long userId) {
        if (projectJoinRequestRepository.findAnotherRequestAlreadyExist(userId).isPresent()) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_SHOULD_BE_ONLY_ONE);
        }
    }
}
