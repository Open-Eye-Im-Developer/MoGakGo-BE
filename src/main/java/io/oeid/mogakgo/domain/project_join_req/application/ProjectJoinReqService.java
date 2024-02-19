package io.oeid.mogakgo.domain.project_join_req.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_MATCHING_USER_TO_ACCEPT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SENDER_TO_ACCEPT;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_JOIN_REQUEST_NOT_FOUND;

import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.matching.application.UserMatchingService;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProjectJoinReqService {

    private final ProjectJoinRequestJpaRepository projectJoinRequestJpaRepository;
    private final UserMatchingService userMatchingService;
    private final MatchingService matchingService;

    private final UserCommonService userCommonService;

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

        // 내가 보낸 대기 중 요청이 있으면 취소 처리
        // 여기서 나는 에러는 클라와 상관이 없으므로 에러가 발생해도 넘어갈 수 있게 처리.
        try {
            cancelMyPendingProjectJoinRequest(tokenUser);
        } catch (ProjectJoinRequestException e) {
            // TODO: 로그 처리
        }

        return matchingId;
    }

    private void cancelMyPendingProjectJoinRequest(User sender) {
        projectJoinRequestJpaRepository.findPendingBySenderId(sender.getId())
            .ifPresent(pendingProjectJoinRequest -> pendingProjectJoinRequest.cancel(sender));
    }

    private ProjectJoinRequest getProjectJoinRequestWithProject(Long projectRequestId) {
        return projectJoinRequestJpaRepository.findByIdWithProject(projectRequestId)
            .orElseThrow(() -> new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_NOT_FOUND));
    }

}
