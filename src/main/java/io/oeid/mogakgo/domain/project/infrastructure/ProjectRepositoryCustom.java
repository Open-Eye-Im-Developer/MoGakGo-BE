package io.oeid.mogakgo.domain.project.infrastructure;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {

    Slice<Project> findPendingProjectsByRegion(
        Long cursorId, Region region, Pageable pageable
    );
    Slice<Project> findByCondition(
        Long cursorId, Long userId, Region region, ProjectStatus projectStatus, Pageable pageable
    );
}
