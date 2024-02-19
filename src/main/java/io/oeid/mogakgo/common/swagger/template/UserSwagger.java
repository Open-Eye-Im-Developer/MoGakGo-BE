package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserSignUpApiRequest;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserProfileCardApiResponse;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserSignUpApiResponse;
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

@Tag(name = "User", description = "회원 관련 API")
@SuppressWarnings("unused")
public interface UserSwagger {

    @Operation(summary = "회원 가입", description = "회원 가입을 할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 가입 성공",
            content = @Content(schema = @Schema(implementation = UserSignUpApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E020103", value = SwaggerUserErrorExamples.INVALID_USER_NAME),
                })),
        @ApiResponse(responseCode = "404", description = "OAuth2 정보가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserSignUpApiResponse> userSignUpApi(
        @Parameter(hidden = true) Long userId,
        UserSignUpApiRequest apiRequest);

    @Operation(summary = "회원 삭제", description = "회원을 삭제할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<Void> userDeleteApi(@Parameter(hidden = true) Long userId);

    @Operation(summary = "회원 조회 [회원 가입]", description = "회원 가입시 회원 정보를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileCardApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserProfileCardApiResponse> userGetApi(@Parameter(hidden = true) Long userId);
}
