package io.oeid.mogakgo.domain.chat.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 API 요청 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatApiReq {

    @Schema(description = "유저 ID", example = "2")
    private Long userId;
    @Schema(description = "메시지", example = "안녕하세요", nullable = true)
    private String message;
}
