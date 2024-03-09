package io.oeid.mogakgo.domain.matching.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingHistoryRes;

public interface MatchingRepositoryCustom {

    CursorPaginationResult<MatchingHistoryRes> getMyMatches(
        Long userId, MatchingStatus matchingStatus, CursorPaginationInfoReq cursorPaginationInfoReq
    );

}
