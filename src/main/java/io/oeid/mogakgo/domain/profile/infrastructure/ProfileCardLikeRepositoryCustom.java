package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;

public interface ProfileCardLikeRepositoryCustom {

    Long getLikeCountByCondition(Long senderId, Long receiverId);
    Long getReceivedLikeCount(Long userId);
    CursorPaginationResult<UserProfileLikeInfoAPIRes> getLikeInfoBySender(
        Long senderId, CursorPaginationInfoReq pageable);
}
