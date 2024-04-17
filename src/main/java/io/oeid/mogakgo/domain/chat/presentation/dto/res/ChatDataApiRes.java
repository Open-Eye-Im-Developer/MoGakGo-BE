package io.oeid.mogakgo.domain.chat.presentation.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "채팅 데이터")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDataApiRes {

    @Schema(description = "채팅 ID")
    private Long id;
    @Schema(description = "보낸 사람 ID")
    private Long senderId;
    @Schema(description = "메시지")
    private String message;
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    public static ChatDataApiRes from(ChatMessage chatMessage) {
        return new ChatDataApiRes(chatMessage.getId(), chatMessage.getSenderId(),
            chatMessage.getMessage(), chatMessage.getCreatedAt());
    }

    public static ChatDataApiRes of(Long id, Long senderId, String message,
        LocalDateTime createdAt) {
        return new ChatDataApiRes(id, senderId, message, createdAt);
    }
}
