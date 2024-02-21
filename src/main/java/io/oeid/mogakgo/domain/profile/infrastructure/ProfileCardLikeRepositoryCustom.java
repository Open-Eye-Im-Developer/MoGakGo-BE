package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
import java.util.Optional;

public interface ProfileCardLikeRepositoryCustom {

    Long getLikeCountByCondition(Long senderId, Long receiverId);
    Long getLikeCount(Long userId);
    CursorPaginationResult<UserProfileLikeInfoAPIRes> getLikeInfoBySender(
        Long senderId, CursorPaginationInfoReq pageable);
    Optional<ProfileCardLike> findBySenderAndReceiver(Long senderId, Long receiverId);
}
