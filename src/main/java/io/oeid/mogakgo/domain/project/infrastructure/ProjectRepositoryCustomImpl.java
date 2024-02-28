package io.oeid.mogakgo.domain.project.infrastructure;

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
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
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
                new UserPublicApiResponse(
                    project.getCreator().getId(),
                    project.getCreator().getUsername(),
                    project.getCreator().getGithubId(),
                    project.getCreator().getAvatarUrl(),
                    project.getCreator().getGithubUrl(),
                    project.getCreator().getBio(),
                    project.getCreator().getJandiRate(),
                    project.getCreator().getAchievement() != null ? project.getCreator().getAchievement().getTitle() : null,
                    project.getCreator().getUserDevelopLanguageTags().stream().map(
                        UserDevelopLanguageTag::getDevelopLanguage).map(String::valueOf).toList(),
                    project.getCreator().getUserWantedJobTags().stream().map(
                        UserWantedJobTag::getWantedJob).map(String::valueOf).toList()
                ),
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
    public List<Region> getDensityRankProjectsByRegion(int limit) {
        return jpaQueryFactory.select(project.creatorInfo.region)
            .from(project)
            .groupBy(project.creatorInfo.region)
            .having(project.creatorInfo.region.count().gt(0L))
            .orderBy(project.creatorInfo.region.count().desc())
            .limit(limit)
            .fetch();
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
        return projectStatus != null ? project.projectStatus.eq(projectStatus) : null;
    }

    private BooleanExpression createdAtEq(LocalDate today) {
        return project.createdAt.year().eq(today.getYear())
            .and(project.createdAt.month().eq(today.getMonthValue()))
            .and(project.createdAt.dayOfMonth().eq(today.getDayOfMonth()));
    }
}
