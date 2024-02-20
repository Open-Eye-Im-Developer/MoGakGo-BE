package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;
import static io.oeid.mogakgo.domain.project_join_req.domain.entity.QProjectJoinRequest.projectJoinRequest;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.projectJoinRequestRes;
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
//        List<projectJoinRequestRes> result = jpaQueryFactory.select(
//                Projections.constructor(
//                    projectJoinRequestRes.class,
//                    projectJoinRequest.id,
//                    projectJoinRequest.sender,
//                    projectJoinRequest.requestStatus
//                )
//            )
//            .from(projectJoinRequest)
//            .join(projectJoinRequest.sender)
//            .where(
//                cursorIdCondition(pageable.getCursorId()),
//                senderIdEq(senderId),
//                projectIdEq(projectId),
//                requestStatusEq(requestStatus)
//            )
//            .limit(pageable.getPageSize() + 1)
//            .fetch();

        List<ProjectJoinRequest> entities = jpaQueryFactory.selectFrom(projectJoinRequest)
            .join(projectJoinRequest.sender).fetchJoin()
            .leftJoin(projectJoinRequest.sender.achievement).fetchJoin()
            .where(
                cursorIdCondition(pageable.getCursorId()),
                senderIdEq(senderId),
                projectIdEq(projectId),
                requestStatusEq(requestStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<projectJoinRequestRes> result = entities.stream().map(
            projectJoinRequest -> new projectJoinRequestRes(
                projectJoinRequest.getId(),
                projectJoinRequest.getSender(),
                projectJoinRequest.getRequestStatus()
            )
        ).toList();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize());
    }

    @Override
    public boolean existsByProjectId(Long projectId) {
        return jpaQueryFactory.selectOne()
            .from(projectJoinRequest)
            .where(projectIdEq(projectId))
            .fetchFirst() != null;
    }

    @Override
    public CursorPaginationResult<ProjectJoinRequestDetailAPIRes> getBySenderIdWithPagination(
        Long senderId, Long projectId, RequestStatus requestStatus, CursorPaginationInfoReq pageable
    ) {
        List<ProjectJoinRequestDetailAPIRes> result = jpaQueryFactory.select(
                Projections.constructor(
                    ProjectJoinRequestDetailAPIRes.class,
                    projectJoinRequest.project.id,
                    projectJoinRequest.project.creator.avatarUrl,
                    Projections.constructor(
                        MeetingInfoResponse.class,
                        projectJoinRequest.project.meetingInfo.meetStartTime,
                        projectJoinRequest.project.meetingInfo.meetEndTime,
                        projectJoinRequest.project.meetingInfo.meetDetail
                    ))
            )
            .from(projectJoinRequest)
            .innerJoin(project).on(projectJoinRequest.project.id.eq(projectId))
            .where(
                cursorIdCondition(pageable.getCursorId()),
                senderIdEq(senderId),
                projectIdEq(projectId),
                requestStatusEq(requestStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result, pageable.getPageSize()
        );
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
