package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserAchievementErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.achievement.presentation.dto.res.NonAchievedDetailAPIRes;
import io.oeid.mogakgo.domain.achievement.presentation.dto.res.UserAchievementDetailAPIRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Achievement", description = "사용자의 업적 조회 관련 API")
public interface AchievementSwagger {

    @Operation(summary = "사용자의 진행중, 달성한 업적 상세 조회", description = "사용자가 자신이 진행중이거나 달성한 업적에 대해 조회할 떄 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "업적 상세 조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E140201", value = SwaggerUserAchievementErrorExamples.ACHIEVEMENT_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            ))
    })
    ResponseEntity<List<UserAchievementDetailAPIRes>> getUserAchievementDetail(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "업적을 조회하는 사용자 ID", required = true) Long id
    );

    @Operation(summary = "사용자의 미달성 업적 상세 조회", description = "사용자가 미달성한 업적에 대해 조회할 떄 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "업적 상세 조회 성공"),
        @ApiResponse(responseCode = "403", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E140201", value = SwaggerUserAchievementErrorExamples.ACHIEVEMENT_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            ))
    })
    ResponseEntity<List<NonAchievedDetailAPIRes>> getAchievementDetailExcludeAchieved(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "업적을 조회하는 사용자 ID", required = true) Long id
    );
}
