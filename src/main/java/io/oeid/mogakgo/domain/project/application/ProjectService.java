package io.oeid.mogakgo.domain.project.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.ALREADY_EXIST_PROGRESS_PROJECT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_MATCHING_USER_TO_CREATE_PROJECT;
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
import io.oeid.mogakgo.domain.matching.application.UserMatchingService;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDensityRankRes;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectInfoAPIRes;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.project_join_req.infrastructure.ProjectJoinRequestJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.projectJoinRequestRes;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProjectService {
    private static final int DENSITY_RANK_LIMIT = 3;

    private final UserJpaRepository userJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final GeoService geoService;
    private final ProjectJoinRequestJpaRepository projectJoinRequestJpaRepository;
    private final UserMatchingService userMatchingService;

    @Transactional
    public Long create(Long userId, ProjectCreateReq request) {
        // 유저 존재 여부 체크
        User tokenUser = getUser(userId);

        // 종료 되지 않은 (PENDING,MATCHED) 프로젝트 카드가 있으면 예외를 발생.
        if (isExistsNotEndProjectCard(tokenUser)) {
            throw new ProjectException(ALREADY_EXIST_PROGRESS_PROJECT);
        }

        // 매칭 중인 프로젝트가 있으면 예외를 발생.
        if (userMatchingService.hasProgressMatching(tokenUser.getId())) {
            throw new ProjectException(INVALID_MATCHING_USER_TO_CREATE_PROJECT);
        }

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

    @Transactional
    public void cancel(Long userId, Long projectId) {
        // 유저 존재 여부 체크
        User user = getUser(userId);

        // 프로젝트 존재 여부 체크
        Project project = getProject(projectId);

        // TODO: 순서를 취소 밑으로 내릴 것인가, 쿼리 변경 할것인가
        // 매칭 준비중이지만 요청이 있을때는 잔디력 감소를 위한 변수
        boolean projectHasReq = projectJoinRequestJpaRepository.existsByProjectId(projectId);

        // 프로젝트 취소
        project.cancel(user, projectHasReq);

        // 프로젝트가 받은 모든 요청 거절
        projectJoinRequestJpaRepository.rejectAllByProjectId(projectId);
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
            null, projectId, null, pageable);
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
        if (projects.getData().size() >= 2) {
            Collections.shuffle(projects.getData().subList(0, projects.getData().size() - 1));
        }
        return projects;
    }

    public CursorPaginationResult<ProjectInfoAPIRes> getByCreatorId(
        Long userId, Long creatorId, CursorPaginationInfoReq pageable
    ) {
        User user = getUser(userId);
        validateProjectCardCreator(user, creatorId);

        return projectJpaRepository.findByCreatorIdWithPagination(creatorId, pageable);
    }

    public ProjectDetailAPIRes getByProjectId(Long userId, Long id, Long projectId) {
        User user = getUser(userId);

        validateProjectCardCreator(user, id);

        return ProjectDetailAPIRes.from(getProject(projectId));
    }

    public ProjectDensityRankRes getDensityRankProjects() {
        List<Region> regionRankList = projectJpaRepository.getDensityRankProjectsByRegion(DENSITY_RANK_LIMIT);

        // 필요한 경우 기본 지역 순위 목록으로 채움
        fillWithDefaultRegionsIfNecessary(regionRankList);

        return new ProjectDensityRankRes(regionRankList);
    }

    private boolean isExistsNotEndProjectCard(User tokenUser) {
        return !projectJpaRepository.findNotEndProjectOneByCreatorId(tokenUser.getId(),
            PageRequest.of(0, 1)).isEmpty();
    }

    private void fillWithDefaultRegionsIfNecessary(List<Region> regionRankList) {
        if (regionRankList.size() < DENSITY_RANK_LIMIT) {
            // 기본 지역 순위에서 이미 리스트에 있는 지역을 제외하고 남은 지역을 추가
            List<Region> defaultRegionsToAdd = Region.getDefaultDensityRank().stream()
                .filter(region -> !regionRankList.contains(region))
                .limit(DENSITY_RANK_LIMIT - regionRankList.size())
                .toList();

            regionRankList.addAll(defaultRegionsToAdd);
        }
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
        Region reqArea = geoService.getRegionAboutCoordinates(lng, lat);
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
