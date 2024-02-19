package io.oeid.mogakgo.domain.project.infrastructure;

import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
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
        List<ProjectDetailAPIRes> result = jpaQueryFactory.select(
                Projections.constructor(
                    ProjectDetailAPIRes.class,
                    project.id,
                    Projections.constructor(
                        UserPublicApiResponse.class,
                        project.creator.username,
                        project.creator.githubId,
                        project.creator.avatarUrl,
                        project.creator.githubUrl,
                        project.creator.bio,
                        project.creator.jandiRate,
                        project.creator.userDevelopLanguageTags,
                        project.creator.userWantedJobTags
                    ),
                    project.projectTags,
                    Projections.constructor(
                        MeetingInfoResponse.class,
                        project.meetingInfo.meetStartTime,
                        project.meetingInfo.meetEndTime,
                        project.meetingInfo.meetDetail
                    )
                )
            )
            .from(project)
            .where(
                cursorIdCondition(pageable.getCursorId()),
                userIdEq(userId),
                regionEq(region),
                projectStatusEq(projectStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? project.id.gt(cursorId) : null;
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
}
