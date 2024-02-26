package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerChatErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
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

@Tag(name = "Chat", description = "채팅 관련 API")
@SuppressWarnings("unused")
public interface ChatSwagger {

    @Operation(summary = "채팅방 목록 조회", description = "사용자의 채팅방 목록을 조회하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음",
        content = @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(name = "E020301", value =  SwaggerUserErrorExamples.USER_NOT_FOUND)
        ))
    })
    ResponseEntity<List<ChatRoomPublicRes>> getChatRoomList(@Parameter(hidden = true) Long userId);

    @Operation(summary = "채팅방 상세 조회", description = "채팅방의 상세 정보를 조회하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "요청한 데이터가 유효하지 않음",
        content = @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND),
                @ExampleObject(name = "E030301", value = SwaggerProjectErrorExamples.PROJECT_NOT_FOUND),
                @ExampleObject(name = "E110301", value = SwaggerChatErrorExamples.CHAT_ROOM_NOT_FOUND)
            }
        ))
    })
    ResponseEntity<ChatRoomDataRes> getChatRoomDetailData(@Parameter(hidden = true) Long userId,
        @Parameter(in = ParameterIn.PATH) String chatRoomId);
}
