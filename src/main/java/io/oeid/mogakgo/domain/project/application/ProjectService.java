package io.oeid.mogakgo.domain.project.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NOT_MATCH_MEET_LOCATION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.domain.geo.application.GeoService;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProjectService {

    private final UserJpaRepository userJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final GeoService geoService;

    @Transactional
    public Long create(ProjectCreateReq request) {
        // 유저 존재 여부 체크
        User user = getUser(request.getCreatorId());

        // 프로젝트 카드에 올라온 미팅 장소와 유저의 리전 정보가 일치하지 않으면 예외를 발생.
        //TODO: project 안에서 할지 고민
        validateMeetLocation(request.getMeetLat(), request.getMeetLng(), user.getRegion());

        // 프로젝트 생성
        Project project = request.toEntity(user);
        projectJpaRepository.save(project);

        return project.getId();
    }

    @Transactional
    public void delete(Long userId, Long projectId) {
        // 유저 존재 여부 체크
        User user = getUser(userId);

        // 프로젝트 존재 여부 체크
        Project project = getProject(projectId);

        // 프로젝트 삭제
        project.delete(user);
    }

    private User getUser(Long userId) {
        return userJpaRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    private Project getProject(Long projectId) {
        return projectJpaRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(PROJECT_NOT_FOUND));
    }

    private void validateMeetLocation(Double lat, Double lng, Region userRegion) {
        Region reqArea = Region.getByAreaCode(
            geoService.getAreaCodeAboutCoordinates(lng, lat));
        if (userRegion != reqArea) {
            throw new ProjectException(NOT_MATCH_MEET_LOCATION);
        }
    }

    private void validateProjectCardCreator(
        User tokenUser, Long creatorId
    ) {
        if (!tokenUser.getId().equals(creatorId)) {
            throw new ProjectException(PROJECT_FORBIDDEN_OPERATION);
        }
    }

}
