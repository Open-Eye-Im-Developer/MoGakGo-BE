package io.oeid.mogakgo.domain.project_join_req.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_JOIN_REQUEST_REGION;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_JOIN_REQUEST_ALREADY_EXIST;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectJoinRequestService {

    private final ProjectJoinRequestJpaRepository projectJoinRequestRepository;
    private final UserJpaRepository userRepository;
    private final ProjectJpaRepository projectRepository;

    public Long create(Long userId, ProjectJoinCreateReq request) {
        User tokenUser = validateToken(userId);
        validateSender(tokenUser, request.getSenderId());
        Project project = validateProjectExist(request.getProjectId());
        validateProjectCreator(project, userId);
        validateUserCertRegion(project, tokenUser);
        validateAlreadyExistRequest(userId, project.getId());

        // 프로젝트 매칭 요청 생성
        ProjectJoinRequest joinRequest = request.toEntity(tokenUser, project);
        projectJoinRequestRepository.save(joinRequest);

        return joinRequest.getId();
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

    private Project validateProjectExist(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(PROJECT_NOT_FOUND));
    }

    private void validateProjectCreator(Project project, Long userId) {
        if (project.getCreator().getId().equals(userId)) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION);
        }
    }

    private void validateAlreadyExistRequest(Long userId, Long projectId) {
        if (projectJoinRequestRepository.findAlreadyExists(userId, projectId).isPresent()) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_ALREADY_EXIST);
        }
    }

    private void validateUserCertRegion(Project project, User tokenUser) {
        if (!tokenUser.getRegion().equals(project.getCreatorInfo().getRegion())) {
            throw new ProjectJoinRequestException(INVALID_PROJECT_JOIN_REQUEST_REGION);
        }
    }
}
