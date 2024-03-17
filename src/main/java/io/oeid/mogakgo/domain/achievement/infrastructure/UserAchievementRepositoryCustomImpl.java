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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                achievement2.requirementType.eq(RequirementType.ACCUMULATE),
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

        if (sql2.size() == ACHIEVEMENT_SIZE) {
            sql1 = sql2;
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

    @Override
    public Long findAvailableAchievementByActivityType(Long userId, ActivityType activityType) {

        Tuple result = jpaQueryFactory.select(
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

        if (result == null) {
            return findMinAchievementIdByActivityType(activityType);
        }

        Long latestId = result.get(0, Long.class);
        Boolean isCompleted = result.get(1, Boolean.class);
        Boolean hasNext = result.get(2, Boolean.class);

        if (Boolean.FALSE.equals(isCompleted)) return latestId;
        return Boolean.TRUE.equals(hasNext) ? latestId + 1L : null;
    }

    // 오늘 날짜를 제외한, Accumulate 타입 업적의 진행 횟수 조회
    @Override
    public Integer getAccumulatedProgressCountByActivity(Long userId, ActivityType activityType) {
        Long result = jpaQueryFactory.select(userActivity.createdAt.count())
            .from(userActivity)
            .innerJoin(achievement).on(achievement.activityType.eq(userActivity.activityType))
            .where(
                userIdEq(userId),
                requirementTypeEq(RequirementType.ACCUMULATE),
                activityTypeEq(activityType),
                createdAtNotEq()
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

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? userActivity.user.id.eq(userId) : null;
    }

    private BooleanExpression requirementTypeEq(RequirementType requirementType) {
        return requirementType != null ? achievement.requirementType.eq(requirementType) : null;
    }

    private BooleanExpression activityTypeEq(ActivityType activityType) {
        return activityType != null ? userActivity.activityType.eq(activityType) : null;
    }

    private BooleanExpression createdAtNotEq() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return userActivity.createdAt.before(startOfDay).or(userActivity.createdAt.after(endOfDay));
    }
}
