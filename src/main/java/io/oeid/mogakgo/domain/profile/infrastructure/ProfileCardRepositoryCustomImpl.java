package io.oeid.mogakgo.domain.profile.infrastructure;

import static io.oeid.mogakgo.domain.profile.domain.entity.QProfileCard.profileCard;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileCardRepositoryCustomImpl implements ProfileCardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<ProfileCard> findByCondition(
        Long cursorId, Long userId, Region region, Pageable pageable
    ) {
        List<ProfileCard> result = jpaQueryFactory.selectFrom(profileCard)
            .where(
                cursorIdEq(cursorId),
                userIdEq(userId),
                regionEq(region)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();
        boolean hasNext = checkLastPage(result, pageable);
        return new SliceImpl<>(result, pageable, hasNext);
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? profileCard.user.region.eq(region) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? profileCard.user.id.eq(userId) : null;
    }

    private BooleanExpression cursorIdEq(Long cursorId) {
        return cursorId != null ? profileCard.id.gt(cursorId) : null;
    }

    private boolean checkLastPage(List<ProfileCard> profileCards, Pageable pageable) {
        if (profileCards.size() > pageable.getPageSize()) {
            profileCards.remove(pageable.getPageSize());
            return true;
        }
        return false;
    }
}
