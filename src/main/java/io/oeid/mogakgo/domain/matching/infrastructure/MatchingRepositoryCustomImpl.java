package io.oeid.mogakgo.domain.matching.infrastructure;

import static io.oeid.mogakgo.domain.matching.domain.entity.QMatching.matching;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingHistoryRes;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchingRepositoryCustomImpl implements MatchingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public CursorPaginationResult<MatchingHistoryRes> getMyMatches(
        Long userId, MatchingStatus matchingStatus, CursorPaginationInfoReq cursorPaginationInfoReq
    ) {
        List<MatchingHistoryRes> matchingHistoryResList = jpaQueryFactory
            .select(
                Projections.constructor(
                    MatchingHistoryRes.class,
                    matching.id,
                    matching.matchingStatus,
                    matching.project.creator.avatarUrl,
                    matching.project.meetingInfo.meetDetail,
                    matching.project.meetingInfo.meetStartTime,
                    matching.project.meetingInfo.meetEndTime
                )
            )
            .from(matching)
            .join(matching.project)
            .where(
                participantInMatching(userId),
                matchingStatusEq(matchingStatus),
                cursorIdCondition(cursorPaginationInfoReq.getCursorId())
            )
            // 최근순
            .orderBy(matching.id.desc())
            .limit(cursorPaginationInfoReq.getPageSize() + 1)
            .fetch();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(matchingHistoryResList,
            cursorPaginationInfoReq.getPageSize());
    }

    @Override
    public Integer findDuplicateMatching(Long userId, Long participantId) {

        Long result = jpaQueryFactory.select(matching.id.count())
            .from(matching)
            .join(matching.project)
            .where(
                participantInMatching(userId),
                participantInMatching(participantId),
                matching.matchingStatus.eq(MatchingStatus.FINISHED)
                        .or(matching.matchingStatus.eq(MatchingStatus.PROGRESS)),
                matching.project.projectStatus.eq(ProjectStatus.FINISHED)
                    .or(matching.project.projectStatus.eq(ProjectStatus.MATCHED))
            )
            .fetchOne();

        return result != null ? Math.toIntExact(result) - 1 : 0;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? matching.id.lt(cursorId) : null;
    }

    private BooleanExpression participantInMatching(Long userId) {
        return userId != null ? (matching.sender.id.eq(userId).or(matching.project.creator.id.eq(
            userId))) : null;
    }

    private BooleanExpression matchingStatusEq(MatchingStatus matchingStatus) {
        return matchingStatus != null ? matching.matchingStatus.eq(matchingStatus) : null;
    }

}
