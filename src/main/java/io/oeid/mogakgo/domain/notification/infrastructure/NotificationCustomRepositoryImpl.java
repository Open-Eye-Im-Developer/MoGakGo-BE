package io.oeid.mogakgo.domain.notification.infrastructure;


import static io.oeid.mogakgo.domain.notification.domain.QNotification.notification;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;
import io.oeid.mogakgo.domain.notification.presentation.vo.NotificationDataVo;
import io.oeid.mogakgo.domain.notification.presentation.vo.NotificationSenderVo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CursorPaginationResult<NotificationPublicApiRes> findByUserIdWithPagination(Long userId,
        CursorPaginationInfoReq pageable) {
        List<NotificationPublicApiRes> result = jpaQueryFactory.select(
                Projections.constructor(
                    NotificationPublicApiRes.class,
                    notification.id,
                    notification.notificationTag,
                    Projections.constructor(
                        NotificationSenderVo.class,
                        notification.sender.username,
                        notification.sender.id,
                        notification.sender.avatarUrl
                    ),
                    Projections.constructor(
                        NotificationDataVo.class,
                        notification.detailData,
                        notification.createdAt
                    )
                )
            )
            .from(notification)
            .join(notification.sender, user)
            .where(
                cursorIdCondition(pageable.getCursorId()),
                notification.receiver.id.eq(userId))
            .orderBy(notification.id.desc())
            .limit(pageable.getPageSize() + 1L)
            .fetch();
        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize());
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? notification.id.lt(cursorId) : null;
    }
}
