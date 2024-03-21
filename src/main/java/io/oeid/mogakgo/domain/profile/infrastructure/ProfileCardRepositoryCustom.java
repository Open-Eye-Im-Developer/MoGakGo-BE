package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileInfoAPIRes;
import java.util.List;

public interface ProfileCardRepositoryCustom {

    CursorPaginationResult<UserProfileInfoAPIRes> findByConditionWithPagination(
        Long userId, Region region, CursorPaginationInfoReq pageable
    );

    List<ProfileCard> findByConditionWithPaginationPublic(Region region, Long cursorId,
        Integer pageSize);
}
