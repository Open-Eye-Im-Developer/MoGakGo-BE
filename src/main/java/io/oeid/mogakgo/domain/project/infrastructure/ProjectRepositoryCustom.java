package io.oeid.mogakgo.domain.project.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;

public interface ProjectRepositoryCustom {

    CursorPaginationResult<ProjectDetailAPIRes> findByConditionWithPagination(
        Long userId, Region region, ProjectStatus projectStatus, CursorPaginationInfoReq pageable
    );
}
