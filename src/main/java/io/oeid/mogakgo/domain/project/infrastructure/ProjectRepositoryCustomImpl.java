package io.oeid.mogakgo.domain.project.infrastructure;

import static io.oeid.mogakgo.domain.matching.domain.entity.QMatching.matching;
import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.ProjectTag;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailInfoAPIRes;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectInfoAPIRes;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CursorPaginationResult<ProjectDetailAPIRes> findByConditionWithPagination(
        Long userId, Region region, ProjectStatus projectStatus, CursorPaginationInfoReq pageable
    ) {
        LocalDate today = LocalDate.now();

        List<Project> entities = jpaQueryFactory.selectFrom(project)
            .innerJoin(project.creator, user)
            .on(project.creator.id.eq(user.id))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                userIdEq(userId),
                regionEq(region),
                projectStatusEq(projectStatus),
                createdAtEq(today)
            )
            // 최근순
            .orderBy(project.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<ProjectDetailAPIRes> result = entities.stream().map(
            project -> new ProjectDetailAPIRes(
                project.getId(),
                UserPublicApiResponse.from(project.getCreator()),
                project.getProjectStatus(),
                project.getProjectTags().stream().map(ProjectTag::getContent).toList(),
                new MeetingInfoResponse(
                    project.getMeetingInfo().getMeetStartTime(),
                    project.getMeetingInfo().getMeetEndTime(),
                    project.getMeetingInfo().getMeetDetail()
                )
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    @Override
    public CursorPaginationResult<ProjectInfoAPIRes> findByCreatorIdWithPagination(
        Long userId, CursorPaginationInfoReq pageable
    ) {
        List<Project> entities = jpaQueryFactory.selectFrom(project)
            .innerJoin(project.creator, user)
            .on(project.creator.id.eq(user.id))
            .where(
                userIdEq(userId),
                deletedAtEq()
            )
            .orderBy(project.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<ProjectInfoAPIRes> result = entities.stream().map(
            project -> new ProjectInfoAPIRes(
                project.getId(),
                project.getProjectStatus(),
                project.getCreator().getAvatarUrl(),
                project.getMeetingInfo().getMeetDetail(),
                project.getMeetingInfo().getMeetStartTime(),
                project.getMeetingInfo().getMeetEndTime()
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    @Override
    public List<Region> getDensityRankProjectsByRegion(int limit) {
        return jpaQueryFactory.select(project.creatorInfo.region)
            .from(project)
            .groupBy(project.creatorInfo.region)
            .having(project.creatorInfo.region.count().gt(0L))
            .orderBy(project.creatorInfo.region.count().desc())
            .limit(limit)
            .fetch();
    }

    @Override
    public ProjectDetailInfoAPIRes findLatestProjectByUserId(Long userId) {

        List<Project> entity = jpaQueryFactory.selectFrom(project)
            .innerJoin(project.creator, user)
            .on(project.creator.id.eq(user.id))
            .where(
                userIdEq(userId),
                projectStatusEq(ProjectStatus.PENDING)
                    .or(projectStatusEq(ProjectStatus.MATCHED)),
                createdAtEq(LocalDate.now())
            )
            .fetch();

        Long matchingId = entity.isEmpty() ? null : jpaQueryFactory.select(matching.id)
            .from(matching)
            .where(matching.project.id.eq(entity.get(0).getId()))
            .fetchOne();

        List<ProjectDetailAPIRes> result = entity.stream().map(
            project -> new ProjectDetailAPIRes(
                project.getId(),
                UserPublicApiResponse.from(project.getCreator()),
                project.getProjectStatus(),
                project.getProjectTags().stream().map(ProjectTag::getContent).toList(),
                new MeetingInfoResponse(
                    project.getMeetingInfo().getMeetStartTime(),
                    project.getMeetingInfo().getMeetEndTime(),
                    project.getMeetingInfo().getMeetDetail()
                )
            )
        ).toList();

        return ProjectDetailInfoAPIRes.of(matchingId, result);
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? project.id.lt(cursorId) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? project.creator.id.eq(userId) : null;
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? project.creatorInfo.region.eq(region) : null;
    }

    private BooleanExpression projectStatusEq(ProjectStatus projectStatus) {
        return project.projectStatus.eq(projectStatus);
    }

    private BooleanExpression createdAtEq(LocalDate today) {
        return project.createdAt.year().eq(today.getYear())
            .and(project.createdAt.month().eq(today.getMonthValue()))
            .and(project.createdAt.dayOfMonth().eq(today.getDayOfMonth()));
    }

    private BooleanExpression deletedAtEq() {
        return project.deletedAt.isNull();
    }
}
