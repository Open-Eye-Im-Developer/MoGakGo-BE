package io.oeid.mogakgo.domain.achievement.infrastructure;

import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement.userAchievement;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity.userActivity;
import static io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement.achievement;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.QUserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.QUserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
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
    QUserAchievement userAchievement1 = new QUserAchievement("userAchievement1");
    QUserActivity userActivity1 = new QUserActivity("userActivity1");

    @Override
    public List<UserAchievementInfoRes> getAchievementInfoAboutUser(Long userId) {

        List<Tuple> sql1 = jpaQueryFactory.select(
                Expressions.numberPath(Long.class, String.valueOf(userId)),
                achievement.id,
                achievement.title,
                achievement.imgUrl,
                achievement.description,
                achievement.progressLevel,
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
                userAchievement.user.id,
                userAchievement.achievement.id,
                userAchievement.achievement.title,
                userAchievement.achievement.imgUrl,
                userAchievement.achievement.description,
                userAchievement.achievement.progressLevel,
                userAchievement.achievement.requirementType,
                userAchievement.achievement.requirementValue,
                Expressions.numberPath(Long.class, String.valueOf(userActivity.createdAt.count())),
                userAchievement.completed
            )
            .from(userAchievement)
            .innerJoin(userAchievement.achievement).on(userAchievement.achievement.id.eq(achievement.id))
            .innerJoin(userActivity).on(userActivity.activityType.eq(userAchievement.achievement.activityType))
            .where(
                userAchievement.user.id.eq(userId),
                userAchievement.user.id.eq(userActivity.user.id),
                userAchievement.achievement.id.in(
                    JPAExpressions.select(achievement1.id.max())
                        .from(userAchievement1)
                        .innerJoin(achievement1).on(achievement1.id.eq(userAchievement1.achievement.id), userAchievement1.user.id.eq(userId))
                        .innerJoin(userActivity1).on(achievement1.activityType.eq(userActivity1.activityType))
                        .where(
                            userAchievement1.achievement.requirementType.eq(RequirementType.ACCUMULATE),
                            userAchievement1.user.id.eq(userActivity1.user.id))
                        .groupBy(userActivity1.activityType)
                )
            )
            .groupBy(
                userAchievement.user.id,
                userAchievement.achievement.id,
                userAchievement.achievement.title,
                userAchievement.achievement.imgUrl,
                userAchievement.achievement.description,
                userAchievement.achievement.progressLevel,
                userAchievement.achievement.requirementType,
                userAchievement.achievement.requirementValue,
                userAchievement.completed
            )
            .fetch();

        if (sql1.size() < ACHIEVEMENT_SIZE) {
            sql1.addAll(sql2);
        }

        if (sql2.size() == ACHIEVEMENT_SIZE) {
            sql1 = sql2;
        }

        return sql1.stream()
            .sorted(Comparator.comparing(tuple -> tuple.get(1, Long.class))).map(
                tuple -> UserAchievementInfoRes.builder()
                    .userId(userId)
                    .achievementId(tuple.get(1, Long.class))
                    .title(tuple.get(2, String.class))
                    .imgUrl(tuple.get(3, String.class))
                    .description(tuple.get(4, String.class))
                    .progressLevel(tuple.get(5, Integer.class))
                    .requirementType(tuple.get(6, RequirementType.class))
                    .requirementValue(tuple.get(7, Integer.class))
                    .progressCount(Integer.valueOf(String.valueOf(tuple.get(8, Long.class))))
                    .completed(tuple.get(9, Boolean.class))
                    .build()
            ).toList();
    }

    @Override
    public Long getAvailableAchievementWithNull(Long userId, ActivityType activityType) {

        Tuple result = findAvailableAchievementByActivityType(userId, activityType);

        if (result == null) {
            return findMinAchievementIdByActivityType(activityType);
        }

        Long latestId = result.get(0, Long.class);
        Boolean isCompleted = result.get(1, Boolean.class);
        Boolean hasNext = result.get(2, Boolean.class);

        if (Boolean.FALSE.equals(isCompleted)) return latestId;
        return Boolean.TRUE.equals(hasNext) ? latestId + 1L : null;
    }

    @Override
    public Long getAvailableAchievementWithoutNull(Long userId, ActivityType activityType) {

        Tuple result = findAvailableAchievementByActivityType(userId, activityType);

        if (result == null) {
            return findMinAchievementIdByActivityType(activityType);
        }

        Long latestId = result.get(0, Long.class);
        Boolean isCompleted = result.get(1, Boolean.class);
        Boolean hasNext = result.get(2, Boolean.class);

        if (Boolean.FALSE.equals(isCompleted)) return latestId;
        return Boolean.TRUE.equals(hasNext) ? latestId + 1L : latestId;
    }

    private Tuple findAvailableAchievementByActivityType(Long userId, ActivityType activityType) {

        return jpaQueryFactory.select(
                userAchievement.achievement.id.max(),
                userAchievement.completed,
                findAvailableNextStep(userAchievement.achievement.id.max(), activityType)
            )
            .from(userAchievement)
            .innerJoin(userActivity).on(userAchievement.user.id.eq(userActivity.user.id))
            .innerJoin(userAchievement.achievement).on(userActivity.activityType.eq(userAchievement.achievement.activityType))
            .where(
                userAchievement.user.id.eq(userId),
                userAchievement.achievement.activityType.eq(activityType)
            )
            .groupBy(userActivity.activityType)
            .fetchOne();
    }

    // 오늘 날짜를 제외한, Accumulate 타입 업적의 진행 횟수 조회
    @Override
    public Integer getAccumulatedProgressCountByActivity(Long userId, ActivityType activityType) {
        Long result = jpaQueryFactory.select(userActivity.createdAt.count())
            .from(achievement)
            .innerJoin(userActivity).on(achievement.activityType.eq(userActivity.activityType), userActivity.user.id.eq(userId))
            .where(
                achievement.id.in(
                JPAExpressions.select(achievement1.id.min())
                    .from(achievement1)
                    .where(
                        achievement1.requirementType.eq(RequirementType.ACCUMULATE),
                        achievement1.activityType.eq(activityType)
                    )
                )
            )
            .fetchOne();

        return result != null ? Math.toIntExact(result) : 0;
    }

    @Override
    public Long findMinAchievementIdByActivityType(ActivityType activityType) {
        return jpaQueryFactory.select(achievement.id.min())
            .from(achievement)
            .where(achievement.activityType.eq(activityType))
            .fetchOne();
    }

    private BooleanExpression findAvailableNextStep(NumberExpression<Long> achievementId,
        ActivityType activityType) {

        return jpaQueryFactory.selectOne()
            .from(achievement)
            .where(
                achievement.activityType.eq(activityType),
                achievement.id.gt(achievementId)
            )
            .exists();
    }
}
