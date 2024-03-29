package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectJoinReqErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectJoinRequestErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingId;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestAPIRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Project Join Request", description = "프로젝트 카드 참여 요청 관련 API")
@SuppressWarnings("unused")
public interface ProjectJoinReqSwagger {

    @Operation(summary = "프로젝트 참가 요청 수락", description = "회원이 본인이 만든 프로젝트 카드에 대한 참가 요청 중 하나를 수락해 매칭을 생성할때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "참가요청 수락 완료. 매칭 완료."),
        @ApiResponse(responseCode = "400", description = "프로젝트 참가 요청을 수락 할 수 없는 상태임",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E050101", value = SwaggerProjectJoinReqErrorExamples.INVALID_PROJECT_STATUS_TO_ACCEPT),
                    @ExampleObject(name = "E050102", value = SwaggerProjectJoinReqErrorExamples.INVALID_PROJECT_REQ_STATUS_TO_ACCEPT),
                    @ExampleObject(name = "E050103", value = SwaggerProjectJoinReqErrorExamples.INVALID_MATCHING_USER_TO_ACCEPT),
                    @ExampleObject(name = "E050104", value = SwaggerProjectJoinReqErrorExamples.INVALID_SENDER_TO_ACCEPT),
                })),
        @ApiResponse(responseCode = "403", description = "요청을 보낸 사람이 요청 수락할 권한이 안됨 (프로젝트 카드 주인이 아님)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E030201", value = SwaggerProjectErrorExamples.PROJECT_FORBIDDEN_OPERATION))),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E050301", value = SwaggerProjectJoinReqErrorExamples.PROJECT_JOIN_REQUEST_NOT_FOUND),
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
                })),
    })
    ResponseEntity<MatchingId> accept(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "프로젝트 참가 요청 ID", required = true) Long projectRequestId
    );

    @Operation(summary = "프로젝트 매칭 요청 생성", description = "회원이 프로젝트 매칭 요청을 생성할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "프로젝트 매칭 요청 생성 성공",
            content = @Content(schema = @Schema(implementation = ProjectJoinRequestAPIRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E090101",
                        value = SwaggerProjectJoinRequestErrorExamples.PROJECT_JOIN_REQUEST_ALREADY_EXIST),
                    @ExampleObject(name = "E090102",
                        value = SwaggerProjectJoinRequestErrorExamples.PROJECT_JOIN_REQUEST_INVALID_REGION),
                    @ExampleObject(name = "E090103",
                        value = SwaggerProjectJoinRequestErrorExamples.ANOTHER_PROJECT_JOIN_REQUEST_ALREADY_EXIST)
                })),
        @ApiResponse(responseCode = "403", description = "프로젝트 매칭 요청 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E050201",
                    value = SwaggerProjectJoinRequestErrorExamples.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION))),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND),
                    @ExampleObject(name = "E030301", value = SwaggerProjectErrorExamples.PROJECT_NOT_FOUND)
                })),
    })
    ResponseEntity<ProjectJoinRequestAPIRes> create(
        @Parameter(hidden = true) Long userId,
        ProjectJoinCreateReq request
    );

    @Operation(summary = "사용자의 프로젝트 요청 리스트 조회", description = "회원이 자신이 보낸 프로젝트 요청 리스트를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 요청 리스트 조회 성공"),
        @ApiResponse(responseCode = "403", description = "본인의 프로젝트 요청만 조회할 수 있음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E050201",
                    value = SwaggerProjectJoinRequestErrorExamples.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION))),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)))
    })
    @Parameters({
        @Parameter(name = "cursorId", description = "기준이 되는 커서 ID", example = "1"),
        @Parameter(name = "pageSize", description = "요청할 데이터 크기", example = "5", required = true),
        @Parameter(name = "sortOrder", description = "정렬 방향", example = "ASC"),
    })
    ResponseEntity<CursorPaginationResult<ProjectJoinRequestDetailAPIRes>> getBySenderIdWithPagination(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "사용자 ID", required = true) Long id,
        @Parameter(hidden = true) CursorPaginationInfoReq pageable
    );

}
