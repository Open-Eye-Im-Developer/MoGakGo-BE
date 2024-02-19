package io.oeid.mogakgo.domain.profile.infrastructure;

import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCard.profileCard;
import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileCardRepositoryCustomImpl implements ProfileCardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CursorPaginationResult<UserPublicApiResponse> findByConditionWithPagination(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {
        List<UserPublicApiResponse> result = jpaQueryFactory.select(
                Projections.constructor(
                    UserPublicApiResponse.class,
                    profileCard.user.id,
                    profileCard.user.username,
                    profileCard.user.githubId,
                    profileCard.user.avatarUrl,
                    profileCard.user.bio,
                    profileCard.user.jandiRate,
                    profileCard.user.achievement.title,
                    profileCard.user.userDevelopLanguageTags,
                    profileCard.user.userWantedJobTags
                )
            )
            .from(profileCard)
            .where(
                cursorIdCondition(pageable.getCursorId()),
                userIdEq(userId),
                regionEq(region)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? profileCard.user.region.eq(region) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? profileCard.user.id.eq(userId) : null;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? project.id.gt(cursorId) : null;
    }
}
