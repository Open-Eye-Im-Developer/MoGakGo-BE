package io.oeid.mogakgo.domain.achievement.infrastructure;

import static io.oeid.mogakgo.domain.user.domain.QUser.user;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement.userAchievement;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity.userActivity;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement.achievement;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.achievement.application.dto.res.AchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAchievementRepositoryCustomImpl implements UserAchievementRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    QAchievement achievement1 = new QAchievement("achievement1");
    QAchievement achievement2 = new QAchievement("achievement2");
    QUserAchievement userAchievement1 = new QUserAchievement("userAchievement1");

    @Override
    public List<UserAchievementInfoRes> getAchievedOrInProcessUserAchievementInfo(Long userId) {

        List<Tuple> result = jpaQueryFactory.select(userActivity.createdAt.count(), userActivity.activityType.stringValue())
            .from(userActivity)
            .innerJoin(userActivity.user, user)
            .where(userActivity.activityType.eq(achievement.activityType))
            .fetch();

        Integer progressCount = Integer.valueOf(String.valueOf(result.get(0).get(0, Long.class)));
        String activityType = result.get(0).get(1, String.class);

        Long achievementId = jpaQueryFactory.select(achievement.id)
            .from(achievement)
            .where(
                achievement.activityType.stringValue().eq(activityType),
                achievement.requirementValue.gt(progressCount)
            )
            .orderBy(achievement.requirementValue.asc())
            .limit(1)
            .fetchOne();

        return jpaQueryFactory.select(
                Projections.constructor(
                    UserAchievementInfoRes.class,
                    userAchievement.user.id,
                    userAchievement.achievement.id,
                    userAchievement.achievement.title,
                    userAchievement.achievement.imgUrl,
                    userAchievement.achievement.description,
                    userAchievement.achievement.requirementType,
                    userAchievement.achievement.requirementValue,
                    Expressions.numberPath(Integer.class, String.valueOf(progressCount)),
                    userAchievement.completed
                )
            )
            .from(userAchievement)
            .innerJoin(userAchievement.user, user).on(userAchievement.user.id.eq(user.id))
            .innerJoin(userAchievement.achievement, achievement).on(userAchievement.achievement.id.eq(achievement.id))
            .where(
                userIdEq(userId),
                achievementIdEq(achievementId)
            )
            .groupBy(userAchievement.user.id, userAchievement.achievement.id)
            .fetch();
    }

    @Override
    public List<AchievementInfoRes> getNonAchievedAchievementInfo(Long userId) {

        NumberPath<Long> id = Expressions.numberPath(Long.class, String.valueOf(userId));

        return jpaQueryFactory.select(
                Projections.constructor(
                    AchievementInfoRes.class,
                    id.as("id"),
                    achievement.id,
                    achievement.title,
                    achievement.imgUrl,
                    achievement.description,
                    achievement.requirementType,
                    achievement.requirementValue
                )
            )
            .from(achievement)
            .where(
                JPAExpressions.selectOne()
                    .from(userAchievement)
                    .where(
                        userAchievement.achievement.id.eq(achievement.id),
                        userAchievement.user.id.eq(id)
                    )
                    .isNull(),
                achievement.id.in(
                    JPAExpressions.select(achievement1.id.min())
                        .from(achievement1)
                        .where(
                            JPAExpressions.selectOne()
                                .from(userAchievement1)
                                .join(achievement2).on(userAchievement1.achievement.id.eq(achievement2.id))
                                .where(
                                    userAchievement1.user.id.eq(id),
                                    achievement2.activityType.stringValue().eq(achievement1.activityType.stringValue())
                                )
                                .isNull()
                        )
                        .groupBy(achievement1.activityType)
                )
            )
            .fetch();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? userAchievement.user.id.eq(userId) : null;
    }

    private BooleanExpression achievementIdEq(Long achievementId) {
        return achievementId != null ? userAchievement.achievement.id.eq(achievementId) : null;
    }
}
