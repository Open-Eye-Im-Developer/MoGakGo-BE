package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerMatchingErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Matching", description = "매칭 관련 API")
@SuppressWarnings("unused")
public interface MatchingSwagger {

    @Operation(summary = "매칭 취소", description = "회원이 현재 진행 중인 매칭을 취소할때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "매칭 취소 완료"),
        @ApiResponse(responseCode = "400", description = "매칭을 취소할 수 없는 상태임",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "E090101", value = SwaggerMatchingErrorExamples.MATCHING_CANCEL_NOT_ALLOWED),
                    @ExampleObject(name = "E030110", value = SwaggerProjectErrorExamples.INVALID_PROJECT_STATUS_TO_FINISH)
                })),
        @ApiResponse(responseCode = "403", description = "요청을 보낸 사람이 매칭 취소할 권한이 안됨 (매칭 참여자가 아님)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "E090201", value = SwaggerMatchingErrorExamples.MATCHING_FORBIDDEN_OPERATION))),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "E090201", value = SwaggerMatchingErrorExamples.MATCHING_NOT_FOUND),
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
                })),
    })
    ResponseEntity<MatchingId> cancel(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매칭 ID", required = true) Long matchingId
    );

}
