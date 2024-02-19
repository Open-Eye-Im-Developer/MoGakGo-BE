package io.oeid.mogakgo.domain.project.infrastructure;

import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import org.springframework.data.domain.Pageable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Project> findByCondition(
        Long cursorId, Long userId, Region region, ProjectStatus projectStatus, Pageable pageable
    ) {
        List<Project> result = jpaQueryFactory.selectFrom(project)
            .where(
                cursorIdEq(cursorId),
                userIdEq(userId),
                regionEq(region),
                projectStatusEq(projectStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();
        boolean hasNext = checkLastPage(result, pageable);
        return new SliceImpl<>(result, pageable, hasNext);
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

    private BooleanExpression cursorIdEq(Long cursorId) {
        return cursorId != null ? project.id.gt(cursorId) : null;
    }

    private boolean checkLastPage(List<Project> projects, Pageable pageable) {
        if (projects.size() > pageable.getPageSize()) {
            projects.remove(pageable.getPageSize());
            return true;
        }
        return false;
    }
}
