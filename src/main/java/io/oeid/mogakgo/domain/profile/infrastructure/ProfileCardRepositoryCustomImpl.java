package io.oeid.mogakgo.domain.profile.infrastructure;

import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCard.profileCard;
import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCardLike.profileCardLike;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileInfoAPIRes;
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileCardRepositoryCustomImpl implements ProfileCardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CursorPaginationResult<UserProfileInfoAPIRes> findByConditionWithPagination(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {

        List<ProfileCard> entities = jpaQueryFactory.selectFrom(profileCard)
            .innerJoin(profileCard.user, user)
            .on(profileCard.user.id.eq(user.id))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                excludeUserId(userId),
                regionEq(region),
                deletedProfileCardEq()
            )
            // 최근순
            .orderBy(profileCard.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<UserProfileInfoAPIRes> result = entities.stream().map(
            profileCard -> new UserProfileInfoAPIRes(
                new UserPublicApiResponse(
                    profileCard.getUser().getId(),
                    profileCard.getUser().getUsername(),
                    profileCard.getUser().getGithubId(),
                    profileCard.getUser().getAvatarUrl(),
                    profileCard.getUser().getGithubUrl(),
                    profileCard.getUser().getBio(),
                    profileCard.getUser().getJandiRate(),
                    profileCard.getUser().getAchievement() != null ? profileCard.getUser().getAchievement().getTitle() : null,
                    profileCard.getUser().getUserDevelopLanguageTags().stream().map(
                        UserDevelopLanguageTag::getDevelopLanguage).map(String::valueOf).toList(),
                    profileCard.getUser().getUserWantedJobTags().stream().map(
                        UserWantedJobTag::getWantedJob).map(String::valueOf).toList()
                ),
                findProfileCardLikeBySenderId(profileCard.getUser().getId(), userId)
                    .isPresent() ? Boolean.TRUE : Boolean.FALSE
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    private Optional<ProfileCardLike> findProfileCardLikeBySenderId(Long receiverId, Long userId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(profileCardLike)
            .join(profileCardLike.receiver)
            .where(
                profileCardLike.receiver.id.eq(receiverId),
                profileCardLike.sender.id.eq(userId)
            )
            .fetchOne());
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? profileCard.user.region.eq(region) : null;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? profileCard.id.lt(cursorId) : null;
    }

    private BooleanExpression excludeUserId(Long userId) {
        return profileCard.user.id.ne(userId);
    }

    private BooleanExpression deletedProfileCardEq() {
        return profileCard.user.deletedAt.isNull();
    }
}
