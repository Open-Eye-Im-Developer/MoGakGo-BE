package io.oeid.mogakgo.domain.profile.infrastructure;

import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCard.profileCard;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
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
        List<ProfileCard> entities = jpaQueryFactory.selectFrom(profileCard)
            .innerJoin(profileCard.user, user)
            .on(profileCard.user.id.eq(user.id))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                userIdEq(userId),
                regionEq(region)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<UserPublicApiResponse> result = entities.stream().map(
            profileCard -> new UserPublicApiResponse(
                profileCard.getUser().getId(),
                profileCard.getUser().getUsername(),
                profileCard.getUser().getGithubId(),
                profileCard.getUser().getAvatarUrl(),
                profileCard.getUser().getGithubUrl(),
                profileCard.getUser().getBio(),
                profileCard.getUser().getJandiRate(),
                profileCard.getUser().getAchievement().getTitle(),
                profileCard.getUser().getUserDevelopLanguageTags().stream().map(
                    UserDevelopLanguageTag::getDevelopLanguage).map(String::valueOf).toList(),
                profileCard.getUser().getUserWantedJobTags().stream().map(
                    UserWantedJobTag::getWantedJob).map(String::valueOf).toList()
            )
        ).toList();

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
        return cursorId != null ? profileCard.id.gt(cursorId) : null;
    }
}
