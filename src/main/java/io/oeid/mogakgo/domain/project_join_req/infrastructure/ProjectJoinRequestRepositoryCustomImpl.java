package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import static io.oeid.mogakgo.domain.project_join_req.domain.entity.QProjectJoinRequest.projectJoinRequest;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.project_join_req.presentation.projectJoinRequestRes;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPreviewRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectJoinRequestRepositoryCustomImpl implements ProjectJoinRequestRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CursorPaginationResult<projectJoinRequestRes> findByConditionWithPagination(
        Long senderId, Long projectId, RequestStatus requestStatus, CursorPaginationInfoReq pageable
    ) {
        List<projectJoinRequestRes> result = jpaQueryFactory.select(
                Projections.constructor(
                    projectJoinRequestRes.class,
                    projectJoinRequest.id,
                    Projections.constructor(
                        UserPreviewRes.class,
                        projectJoinRequest.sender.id,
                        projectJoinRequest.sender.username,
                        projectJoinRequest.sender.avatarUrl
                    ),
                    projectJoinRequest.requestStatus
                )
            )
            .from(projectJoinRequest)
            .join(projectJoinRequest.sender)
            .where(
                cursorIdCondition(pageable.getCursorId()),
                senderIdEq(senderId),
                projectIdEq(projectId),
                requestStatusEq(requestStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize());
    }

    private BooleanExpression senderIdEq(Long senderId) {
        return senderId != null ? projectJoinRequest.sender.id.eq(senderId) : null;
    }

    private BooleanExpression projectIdEq(Long projectId) {
        return projectId != null ? projectJoinRequest.project.id.eq(projectId) : null;
    }

    private BooleanExpression requestStatusEq(RequestStatus requestStatus) {
        return requestStatus != null ? projectJoinRequest.requestStatus.eq(requestStatus) : null;
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId != null ? projectJoinRequest.id.gt(cursorId) : null;
    }

}
