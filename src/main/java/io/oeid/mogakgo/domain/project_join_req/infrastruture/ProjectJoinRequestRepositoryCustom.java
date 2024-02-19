package io.oeid.mogakgo.domain.project_join_req.infrastruture;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.project_join_req.presentation.projectJoinRequestRes;

public interface ProjectJoinRequestRepositoryCustom {

    CursorPaginationResult<projectJoinRequestRes> findByConditionWithPagination(
        Long senderId, Long projectId, RequestStatus requestStatus, CursorPaginationInfoReq pageable
    );
}
