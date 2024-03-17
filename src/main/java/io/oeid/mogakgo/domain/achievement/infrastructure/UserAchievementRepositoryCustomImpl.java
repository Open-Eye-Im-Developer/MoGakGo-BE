package io.oeid.mogakgo.domain.achievement.infrastructure;

import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement.userAchievement;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity.userActivity;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement.achievement;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAchievementRepositoryCustomImpl implements UserAchievementRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private static final int ACHIEVEMENT_SIZE = 14;

    QAchievement achievement1 = new QAchievement("achievement1");
    QAchievement achievement2 = new QAchievement("achievement2");
    QAchievement achievement3 = new QAchievement("achievement3");

    QUserAchievement userAchievement1 = new QUserAchievement("userAchievement1");
    QUserAchievement userAchievement2 = new QUserAchievement("userAchievement2");

    QUserActivity userActivity1 = new QUserActivity("userActivity1");

    @Override
    public List<UserAchievementInfoRes> getAchievementInfoAboutUser(Long userId) {

        List<Tuple> sql1 = jpaQueryFactory.select(
            Expressions.numberPath(Long.class, String.valueOf(userId)),
            achievement.id,
            achievement.title,
            achievement.imgUrl,
            achievement.description,
            achievement.requirementType,
            achievement.requirementValue,
            Expressions.numberPath(Long.class, String.valueOf(0)),
            Expressions.booleanPath(String.valueOf(false))
        )
        .from(achievement)
        .leftJoin(userAchievement)
            .on(userAchievement.achievement.id.eq(achievement.id),
                userAchievement.user.id.eq(userId))
        .where(
            userAchievement.user.id.isNull(),
            achievement.id.in(
                JPAExpressions.select(achievement1.id.min())
                    .from(achievement1)
                    .groupBy(achievement1.activityType)
            )
        )
        .fetch();

        List<Tuple> sql2 = jpaQueryFactory.select(
            userAchievement1.user.id,
            achievement2.id,
            achievement2.title,
            achievement2.imgUrl,
            achievement2.description,
            achievement2.requirementType,
            achievement2.requirementValue,
            Expressions.numberPath(Long.class, String.valueOf(userActivity.createdAt.count())),
            userAchievement1.completed
        )
        .from(achievement2)
        .innerJoin(userAchievement1).on(
            userAchievement1.achievement.id.eq(achievement2.id),
            userAchievement1.user.id.eq(userId)
        )
        .innerJoin(userActivity).on(userActivity.activityType.eq(achievement2.activityType))
        .where(
            achievement2.id.in(
                JPAExpressions.select(achievement3.id.max())
                    .from(userActivity1)
                    .innerJoin(achievement3)
                        .on(achievement3.activityType.eq(userActivity1.activityType),
                            userActivity.user.id.eq(userId))
                    .innerJoin(userAchievement2).
                        on(userAchievement2.achievement.id.eq(achievement3.id))
                    .groupBy(userActivity1.activityType)
            )
        )
        .fetch();

        if (sql1.size() < ACHIEVEMENT_SIZE) {
            sql1.addAll(sql2);
        }

        return sql1.stream()
            .sorted(Comparator.comparing(tuple -> tuple.get(1, Long.class))).map(
            tuple -> new UserAchievementInfoRes(
                userId,
                tuple.get(1, Long.class),
                tuple.get(2, String.class),
                tuple.get(3, String.class),
                tuple.get(4, String.class),
                tuple.get(5, RequirementType.class),
                tuple.get(6, Integer.class),
                Integer.valueOf(String.valueOf(tuple.get(6, Long.class))),
                tuple.get(8, Boolean.class)
            )
        ).toList();
    }
}
