package io.oeid.mogakgo.domain.achievement.infrastructure;

import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity.userActivity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserActivityRepositoryCustomImpl implements UserActivityRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserActivity> getActivityHistoryByActivityType(Long userId, ActivityType activityType, Integer limit) {
        return jpaQueryFactory
            .selectFrom(userActivity)
            .where(
                userActivity.user.id.eq(userId),
                userActivity.activityType.eq(activityType),
                userActivity.deletedAt.isNull()
            )
            .limit(limit)
            .fetch();
    }
}
