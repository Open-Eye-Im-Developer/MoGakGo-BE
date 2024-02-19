package io.oeid.mogakgo.domain.project.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;
import static io.oeid.mogakgo.exception.code.ErrorCode400.NOT_MATCH_MEET_LOCATION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.application.GeoService;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.presentation.projectJoinRequestRes;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import java.util.Collections;
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
    private final ProjectJoinRequestJpaRepository projectJoinRequestJpaRepository;

    @Transactional
    public Long create(Long userId, ProjectCreateReq request) {
        // 유저 존재 여부 체크
        User tokenUser = getUser(userId);
        // 프로젝트 카드 생성자와 토큰 유저가 다르면 예외를 발생.
        validateProjectCardCreator(tokenUser, request.getCreatorId());

        // 프로젝트 카드에 올라온 미팅 장소와 유저의 리전 정보가 일치하지 않으면 예외를 발생.
        //TODO: project 안에서 할지 고민
        validateMeetLocation(request.getMeetLat(), request.getMeetLng(), tokenUser.getRegion());

        // 프로젝트 생성
        Project project = request.toEntity(tokenUser);
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

    public void cancel(Long userId, Long projectId) {
        // 유저 존재 여부 체크
        User user = getUser(userId);

        // 프로젝트 존재 여부 체크
        Project project = getProject(projectId);

        // 매칭이 되었거나, 매칭 준비중이지만 요청이 있을때는 잔디력 감소를 위한 변수
        boolean projectHasReq = projectJoinRequestJpaRepository.existsByProjectId(projectId);

        // 프로젝트 취소
        project.cancel(user, projectHasReq);
    }

    public CursorPaginationResult<projectJoinRequestRes> getJoinRequest(
        Long userId, Long projectId, CursorPaginationInfoReq pageable
    ) {
        // 유저 존재 여부 체크
        User user = getUser(userId);

        // 프로젝트 존재 여부 체크
        Project project = getProject(projectId);

        // 본인만 본인의 프로젝트 참가 요청을 조회할 수 있음
        // TODO : project exception 인지 project join request exception 인지 확인
        try {
            project.validateCreator(user);
        } catch (ProjectException e) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION);
        }

        // 프로젝트 참가 요청 조회
        return projectJoinRequestJpaRepository.findByConditionWithPagination(
            null, projectId, null, null);
    }

    // 선택한 구역에 대한 프로젝트 카드 리스트 랜덤 조회
    public CursorPaginationResult<ProjectDetailAPIRes> getRandomOrderedProjectsByRegion(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {
        getUser(userId);

        // 선택한 구역의 서비스 지역 여부 체크
        validateRegionCoverage(region);

        // 선택한 구역에 대해 Pending 상태인 프로젝트 리스트를 조회할 수 있음
        CursorPaginationResult<ProjectDetailAPIRes> projects = projectJpaRepository
            .findByConditionWithPagination(
                null, region, ProjectStatus.PENDING, pageable
        );

        // 요청할 때마다 랜덤 정렬
        Collections.shuffle(projects.getData());
        return projects;
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

    private void validateRegionCoverage(Region region) {
        if (Region.getByAreaCode(region.getAreaCode()) == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }
}
