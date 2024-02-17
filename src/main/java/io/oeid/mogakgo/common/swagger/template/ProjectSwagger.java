package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectIdRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Project Card", description = "프로젝트 카드 관련 API")
@SuppressWarnings("unused")
public interface ProjectSwagger {

    @Operation(summary = "프로젝트 카드 생성", description = "회원이 프로젝트 카드를 생성할 때 사용하는 API"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "프로젝트 카드 생성 성공",
            content = @Content(schema = @Schema(implementation = ProjectIdRes.class))),
        @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E030101", value = SwaggerProjectErrorExamples.INVALID_PROJECT_MEETING_TIME),
                    @ExampleObject(name = "E030102", value = SwaggerProjectErrorExamples.INVALID_PROJECT_TAG_COUNT),
                    @ExampleObject(name = "E030103", value = SwaggerProjectErrorExamples.INVALID_PROJECT_TAG_CONTENT_LENGTH),
                    @ExampleObject(name = "E030104", value = SwaggerProjectErrorExamples.INVALID_PROJECT_NULL_DATA),
                    @ExampleObject(name = "E030105", value = SwaggerProjectErrorExamples.INVALID_PROJECT_MEET_LOCATION),
                })),
    })
    ResponseEntity<ProjectIdRes> create(ProjectCreateReq request);
}
