package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectJoinRequestErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestAPIRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Project Join Request", description = "프로젝트 매칭 요청 관련 API")
public interface ProjectJoinRequestSwagger {

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
                        value = SwaggerProjectJoinRequestErrorExamples.PROJECT_JOIN_REQUEST_INVALID_REGION)
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
        @ApiResponse(responseCode = "200", description = "프로젝트 요청 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = ProjectJoinRequestDetailAPIRes.class))),
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
    ResponseEntity<CursorPaginationResult<ProjectJoinRequestDetailAPIRes>> getBySenderIdWithPagination(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "사용자 ID", required = true) Long id,
        @Parameter(hidden = true) CursorPaginationInfoReq pageable
    );

}
