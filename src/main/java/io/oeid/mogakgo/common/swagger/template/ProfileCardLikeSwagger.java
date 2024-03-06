package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProfileCardErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProfileCardLikeErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCancelAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeAPIRes;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeCreateAPIRes;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
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

@Tag(name = "Profile Card Like", description = "프로필 카드 찔러보기 관련 API")
public interface ProfileCardLikeSwagger {

    @Operation(summary = "관심 있는 프로필 카드에 대해 찔러보기 요청 생성", description = "사용자가 관심 있는 프로필 카드에 찔러보기 요청을 보낼 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "찔러보기 요청 생성 성공",
            content = @Content(schema = @Schema(implementation = UserProfileLikeCreateAPIRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E040102", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_ALREADY_EXIST),
                    @ExampleObject(name = "E040103", value = SwaggerProfileCardLikeErrorExamples.INVALID_PROFILE_CARD_LIKE_RECEIVER_INFO)
                }
            )),
        @ApiResponse(responseCode = "403", description = "본인이 아닌 프로필 카드에 대한 찔러보기 요청 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E040101", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            )),
    })
    ResponseEntity<UserProfileLikeCreateAPIRes> create(
        @Parameter(hidden = true) Long userId,
        UserProfileLikeCreateAPIReq request
    );

    @Operation(summary = "사용자가 받은 찔러보기 요청 수 조회", description = "사용자가 자신이 받은 찔러보기 요청 수를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "찔러보기 요청 수 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileLikeAPIRes.class))),
        @ApiResponse(responseCode = "403", description = "본인이 아닌 프로필 카드에 대한 찔러보기 요청 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E040101", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            )),
    })
    ResponseEntity<UserProfileLikeAPIRes> getProfileLikeCountByReceiver(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "'찔러보기' 요청을 조회하는 사용자 ID", required = true) Long id
    );

    @Operation(summary = "사용자가 보낸 찔러보기 요청 수 조회", description = "사용자가 자신이 보낸 찔러보기 요청 수를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "찔러보기 요청 수 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileLikeAPIRes.class))),
        @ApiResponse(responseCode = "403", description = "본인이 아닌 프로필 카드에 대한 찔러보기 요청 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E040101", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            )),
    })
    ResponseEntity<UserProfileLikeAPIRes> getProfileLikeCountBySender(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "'찔러보기' 요청을 조회하는 사용자 ID", required = true) Long id
    );

    @Operation(summary = "사용자가 보낸 찔러보기 요청 상세 리스트 조회", description = "사용자가 자신이 보낸 찔러보기 요청 상세 리스트 정보를 조회할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "찔러보기 요청 상세 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileLikeInfoAPIRes.class))),
        @ApiResponse(responseCode = "403", description = "본인의 프로필 카드에 대한 찔러보기 요청만 조회할 수 있음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E040101", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION)
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
            )),
    })
    @Parameters({
        @Parameter(name = "cursorId", description = "기준이 되는 커서 ID", example = "1"),
        @Parameter(name = "pageSize", description = "요청할 데이터 크기", example = "5", required = true),
        @Parameter(name = "sortOrder", description = "정렬 방향", example = "ASC"),
    })
    ResponseEntity<CursorPaginationResult<UserProfileLikeInfoAPIRes>> getProfileLikeInfoBySender(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "'찔러보기' 요청을 조회하는 사용자 ID", required = true) Long id,
        @Parameter(hidden = true) CursorPaginationInfoReq pageable
    );

    @Operation(summary = "사용자의 '찔러보기' 요청 취소", description = "사용자가 자신이 보낸 '찔러보기' 요청을 취소할 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "'찔러보기' 요청 취소 성공"),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E040105", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_LIKE_NOT_EXIST),
                    @ExampleObject(name = "E040104", value = SwaggerProfileCardErrorExamples.PROFILE_CARD_LIKE_AMOUNT_IS_ZERO),
                    @ExampleObject(name = "E020107", value = SwaggerUserErrorExamples.USER_AVAILABLE_LIKE_COUNT_IS_ZERO)
                }
            )),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND),
                    @ExampleObject(name = "E040301", value = SwaggerProfileCardLikeErrorExamples.PROFILE_CARD_NOT_FOUND)
                }
            )),
    })
    ResponseEntity<Void> cancel(
        @Parameter(hidden = true) Long userId,
        UserProfileLikeCancelAPIReq request
    );
}
