package io.oeid.mogakgo.domain.notification.infrastructure;


import static io.oeid.mogakgo.domain.notification.domain.QNotification.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.notification.domain.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Notification> findByUserIdWithPagination(Long userId, Long cursorId, int pageSize) {
        return jpaQueryFactory.selectFrom(notification)
            .where(cursorIdCondition(cursorId), notification.user.id.eq(userId))
            .orderBy(notification.id.desc()).limit(pageSize + 1L).fetch();
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? notification.id.lt(cursorId) : null;
    }
}
