package io.oeid.mogakgo.domain.notification.infrastructure;


import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;

public interface NotificationCustomRepository {

    CursorPaginationResult<NotificationPublicApiRes> findByUserIdWithPagination(Long userId, CursorPaginationInfoReq pageable);
}
