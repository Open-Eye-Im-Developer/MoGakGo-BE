package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerAchievementErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserAchievementErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.user.application.dto.res.UserJandiRateRes;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserAchievementUpdateApiRequest;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserSignUpApiReq;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserUpdateApiReq;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserAchievementUpdateApiResponse;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserDevelopLanguageApiRes;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserMatchingStatus;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserSignUpApiResponse;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserUpdateApiRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
                    @ExampleObject(name = "E020105", value = SwaggerUserErrorExamples.USER_WANTED_JOB_DUPLICATE)
                })),
        @ApiResponse(responseCode = "404", description = "OAuth2 정보가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserSignUpApiResponse> userSignUpApi(
        @Parameter(hidden = true) Long userId,
        UserSignUpApiReq apiRequest);

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
            content = @Content(schema = @Schema(implementation = UserPublicApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserPublicApiResponse> userGetApi(@Parameter(hidden = true) Long userId);


    @Operation(summary = "회원 개발 언어 조회", description = "회원의 주요 개발 언어를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 개발 언어 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<List<UserDevelopLanguageApiRes>> userDevelopLanguageApi(
        @Parameter(hidden = true) Long userId);

    @Operation(summary = "매칭 상태 조회", description = "매칭 상태를 조회할 때 사용하는 API")
    @ApiResponse(responseCode = "200", description = "매칭 상태 조회 성공")
    ResponseEntity<UserMatchingStatus> userMatchingStatusApi(@Parameter(hidden = true) Long userId);

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = UserUpdateApiRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E020103", value = SwaggerUserErrorExamples.INVALID_USER_NAME),
                    @ExampleObject(name = "E020105", value = SwaggerUserErrorExamples.USER_WANTED_JOB_DUPLICATE),
                    @ExampleObject(name = "E020109", value = SwaggerUserErrorExamples.USER_AVATAR_URL_NOT_NULL)
                })),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserUpdateApiRes> userUpdateApi(@Parameter(hidden = true) Long userId,
        UserUpdateApiReq request);


    @Operation(summary = "회원 잔디 점수 조회", description = "회원의 잔디 점수를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 잔디 점수 조회 성공",
            content = @Content(schema = @Schema(implementation = UserJandiRateRes.class))),
        @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserJandiRateRes> userJandiRateApi(@Parameter(in = ParameterIn.PATH) Long userId);

    @Operation(summary = "사용자의 대표 업적 변경", description = "사용자가 자신의 대표 업적을 변경하고 싶을 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "대표 업적 변경 성공"),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E140101", value = SwaggerUserAchievementErrorExamples.NON_ACHIEVED_USER_ACHIEVEMENT),
                    @ExampleObject(name = "E140102", value = SwaggerUserAchievementErrorExamples.ACHIEVEMENT_SHOULD_BE_DIFFERENT)
                }
            )),
        @ApiResponse(responseCode = "403", description = "사용자의 대표 업적을 변경할 권한이 없음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020201", value = SwaggerUserErrorExamples.USER_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E130301", value = SwaggerAchievementErrorExamples.ACHIEVEMENT_NOT_FOUND),
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
                }
            ))
    })
    ResponseEntity<UserAchievementUpdateApiResponse> updateUserMainAchievement(
        @Parameter(hidden = true) Long userId,
        UserAchievementUpdateApiRequest request
    );
}
