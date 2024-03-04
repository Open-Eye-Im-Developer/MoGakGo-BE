package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerNotificationErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCheckedRes;
import io.oeid.mogakgo.domain.notification.presentation.dto.req.FCMTokenApiRequest;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "알림 관련 API")
@SuppressWarnings("unused")
public interface NotificationSwagger {

    @Operation(summary = "FCM 토큰 저장", description = "회원의 FCM 토큰을 저장할 때 사용하는 API")
    @ApiResponse(responseCode = "200", description = "FCM 토큰 저장 성공")
    @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음", content = @Content(
        mediaType = APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
            @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
        })
    )
    ResponseEntity<Void> manageFCMToken(@Parameter(hidden = true) Long userId,
        FCMTokenApiRequest request);

    @Operation(summary = "알림 조회", description = "회원의 알림을 조회할 때 사용하는 API")
    @ApiResponse(responseCode = "200", description = "알림 조회 성공")
    @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음", content = @Content(
        mediaType = APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
            @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND)
        })
    )
    @Parameters({
        @Parameter(name = "cursorId", description = "기준이 되는 커서 ID", example = "1"),
        @Parameter(name = "pageSize", description = "요청할 데이터 크기", example = "5", required = true),
        @Parameter(name = "sortOrder", description = "정렬 방향", example = "ASC"),
    })
    ResponseEntity<CursorPaginationResult<NotificationPublicApiRes>> getByUserId(
        @Parameter(hidden = true) Long id,
        @Parameter(hidden = true) CursorPaginationInfoReq pageable);

    @Operation(summary = "알림 확인", description = "회원의 알림을 확인할 때 사용하는 API")
    @ApiResponse(responseCode = "200", description = "알림 확인 성공")
    @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음", content = @Content(
        mediaType = APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
            @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND),
            @ExampleObject(name = "E060302", value = SwaggerNotificationErrorExamples.NOTIFICATION_NOT_FOUND)
        })
    )
    ResponseEntity<NotificationCheckedRes> markCheckedNotification(
        @Parameter(hidden = true) Long userId,
        @Parameter(in = ParameterIn.PATH) Long notificationId);
}
