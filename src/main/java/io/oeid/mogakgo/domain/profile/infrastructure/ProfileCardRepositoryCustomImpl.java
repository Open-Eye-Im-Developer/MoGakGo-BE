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
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileInfoAPIRes;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
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
            .distinct()
            .innerJoin(profileCard.user, user)
            .on(profileCard.user.id.eq(user.id))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                excludeUserId(userId),
                regionEq(region),
                deletedProfileCardEq()
            )
            // 최근순
            .orderBy(user.id.desc())
            .limit(pageable.getPageSize() + 1L)
            .fetch();

        List<UserProfileInfoAPIRes> result = entities.stream().map(
            profileCard -> new UserProfileInfoAPIRes(
                UserPublicApiResponse.from(profileCard.getUser()),
                validateProfileCardLikeBySenderId(profileCard.getUser().getId(), userId)
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    public List<ProfileCard> findByConditionWithPaginationPublic(@NonNull Region region,
        Long cursorId, @NonNull Integer pageSize) {
        return jpaQueryFactory.selectFrom(profileCard)
            .innerJoin(profileCard.user, user)
            .on(profileCard.user.id.eq(user.id))
            .where(
                cursorIdCondition(cursorId),
                regionEq(region),
                deletedProfileCardEq()
            )
            .orderBy(user.id.desc())
            .limit(pageSize + 1L).fetch();
    }

    private Boolean validateProfileCardLikeBySenderId(Long receiverId, Long userId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(profileCardLike)
            .join(profileCardLike.receiver)
            .where(
                profileCardLike.receiver.id.eq(receiverId),
                profileCardLike.sender.id.eq(userId)
            )
            .fetchOne()).isPresent();
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? profileCard.user.region.eq(region) : null;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? user.id.lt(cursorId) : null;
    }

    private BooleanExpression excludeUserId(Long userId) {
        return profileCard.user.id.ne(userId);
    }

    private BooleanExpression deletedProfileCardEq() {
        return profileCard.user.deletedAt.isNull();
    }
}
