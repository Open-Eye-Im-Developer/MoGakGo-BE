package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestRes;

public interface ProjectJoinRequestRepositoryCustom {

    CursorPaginationResult<ProjectJoinRequestRes> findByConditionWithPagination(
        Long senderId, Long projectId, RequestStatus requestStatus, CursorPaginationInfoReq pageable
    );

    boolean existsByProjectId(Long projectId);

    CursorPaginationResult<ProjectJoinRequestDetailAPIRes> getBySenderIdWithPagination(
        Long senderId, Long projectId, RequestStatus requestStatus, CursorPaginationInfoReq pageable
    );
}
