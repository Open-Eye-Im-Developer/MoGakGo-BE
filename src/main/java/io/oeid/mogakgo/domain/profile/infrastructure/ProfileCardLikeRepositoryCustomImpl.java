package io.oeid.mogakgo.domain.profile.infrastructure;

import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCardLike.profileCardLike;
import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCard.profileCard;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
import io.oeid.mogakgo.domain.user.domain.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileCardLikeRepositoryCustomImpl implements ProfileCardLikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long getLikeCountByCondition(Long senderId, Long receiverId) {

        QUser sender = new QUser("sender");
        QUser receiver = new QUser("receiver");

        return jpaQueryFactory
            .select(profileCardLike.count())
            .from(profileCardLike)
            .innerJoin(profileCardLike.sender, sender)
            .innerJoin(profileCardLike.receiver, receiver)
            .where(
                senderIdEq(senderId),
                receiveridEq(receiverId)
            )
            .fetchOne();
    }

    @Override
    public Long getReceivedLikeCount(Long userId) {
        return jpaQueryFactory.select(profileCard.totalLikeAmount)
            .from(profileCard)
            .innerJoin(profileCard.user, user)
            .where(profileCard.user.id.eq(userId))
            .fetchOne();
    }

    @Override
    public CursorPaginationResult<UserProfileLikeInfoAPIRes> getLikeInfoBySender(
        Long senderId, CursorPaginationInfoReq pageable
    ) {
        List<ProfileCardLike> entities = jpaQueryFactory.selectFrom(profileCardLike)
            .innerJoin(profileCardLike.sender, user).on(profileCardLike.sender.id.eq(user.id))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                senderIdEq(senderId)
            )
            // 최근순
            .orderBy(profileCardLike.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<UserProfileLikeInfoAPIRes> result = entities.stream().map(
            profileCardLike -> new UserProfileLikeInfoAPIRes(
                profileCardLike.getReceiver().getId(),
                profileCardLike.getReceiver().getUsername(),
                profileCardLike.getReceiver().getAvatarUrl(),
                profileCardLike.getCreatedAt()
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
    }

    private BooleanExpression senderIdEq(Long senderId) {
        return senderId != null ? profileCardLike.sender.id.eq(senderId) : null;
    }

    private BooleanExpression receiveridEq(Long receiverId) {
        return receiverId != null ? profileCardLike.receiver.id.eq(receiverId) : null;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? profileCardLike.id.lt(cursorId) : null;
    }
}
