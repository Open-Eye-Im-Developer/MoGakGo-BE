package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.user.application.dto.res.UserProfileResponse;

public interface ProfileCardRepositoryCustom {

    CursorPaginationResult<UserProfileResponse> findByConditionWithPagination(
        Long userId, Region region, CursorPaginationInfoReq pageable
    );
}
