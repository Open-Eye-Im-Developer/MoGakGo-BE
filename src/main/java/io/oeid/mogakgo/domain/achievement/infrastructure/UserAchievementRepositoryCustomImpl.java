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
import io.oeid.mogakgo.domain.achievement.domain.entity.QAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.presentation.dto.res.UserAchievementDetailInfoRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAchievementRepositoryCustomImpl implements UserAchievementRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private static final int ACHIEVEMENT_SIZE = 14;

    QAchievement achievement1 = new QAchievement("achievement1");

    @Override
    public List<UserAchievementDetailInfoRes> getAchievementInfoAboutUser(Long userId) {

        List<Tuple> sql1 = jpaQueryFactory
            .select(
                achievement.id,
                Expressions.numberPath(Long.class, String.valueOf(userId)),
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
            .leftJoin(userActivity)
            .on(achievement.activityType.eq(userActivity.activityType),
                userActivity.user.id.eq(userId))
            .where(
                userActivity.id.isNull(),
                achievement.id.in(
                    JPAExpressions.select(achievement1.id.min())
                        .from(achievement1)
                        .groupBy(achievement1.activityType)
                )
            )
            .fetch();

        List<Tuple> sql2 = jpaQueryFactory
            .select(
                userAchievement.achievement.id,
                userAchievement.user.id,
                userAchievement.achievement.title,
                userAchievement.achievement.imgUrl,
                userAchievement.achievement.description,
                userAchievement.achievement.progressLevel,
                userAchievement.achievement.requirementType,
                userAchievement.achievement.requirementValue,
                Expressions.numberPath(Long.class, String.valueOf(userActivity.createdAt.countDistinct())),
                userAchievement.completed).distinct()
            .from(userAchievement)
            .innerJoin(userAchievement.achievement)
            .innerJoin(userActivity).on(userActivity.activityType.eq(userAchievement.achievement.activityType),
                userAchievement.user.id.eq(userActivity.user.id))
            .where(
                userAchievement.user.id.eq(userId),
                userAchievement.achievement.requirementType.eq(RequirementType.ACCUMULATE)
            )
            .groupBy(userActivity.activityType)
            .fetch();

        if (sql1.size() < ACHIEVEMENT_SIZE) {
            sql1.addAll(sql2);
        }

        if (sql2.size() == ACHIEVEMENT_SIZE) {
            sql1 = sql2;
        }

        return sql1.stream().map(
            tuple -> UserAchievementDetailInfoRes.builder()
                .achievementId(tuple.get(0, Long.class))
                .userId(userId)
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

        return jpaQueryFactory
            .select(
                userAchievement.achievement.id,
                userAchievement.completed,
                findAvailableNextStep(userAchievement.achievement.id, activityType)
            )
            .from(userAchievement)
            .innerJoin(userActivity).on(userAchievement.user.id.eq(userActivity.user.id))
            .where(
                userAchievement.user.id.eq(userId),
                userActivity.activityType.eq(activityType)
            )
            .groupBy(userActivity.activityType)
            .fetchOne();
    }

    // 오늘 날짜를 제외한, Accumulate 타입 업적의 진행 횟수 조회
    @Override
    public Integer getAccumulatedProgressCountByActivity(Long userId, ActivityType activityType) {
        Long result = jpaQueryFactory
            .select(userActivity.createdAt.countDistinct())
            .from(userActivity)
            .innerJoin(achievement).on(userActivity.activityType.eq(achievement.activityType),
                userActivity.user.id.eq(userId))
            .where(
                achievement.requirementType.eq(RequirementType.ACCUMULATE),
                userActivity.activityType.eq(activityType)
            )
            .fetchOne();

        return result != null ? Math.toIntExact(result) : 0;
    }

    @Override
    public Long findMinAchievementIdByActivityType(ActivityType activityType) {
        return jpaQueryFactory
            .select(achievement.id.min())
            .from(achievement)
            .where(achievement.activityType.eq(activityType))
            .fetchOne();
    }

    private BooleanExpression findAvailableNextStep(NumberExpression<Long> achievementId,
        ActivityType activityType) {

        return jpaQueryFactory
            .selectOne()
            .from(achievement)
            .where(
                achievement.activityType.eq(activityType),
                achievement.id.gt(achievementId)
            )
            .exists();
    }
}
