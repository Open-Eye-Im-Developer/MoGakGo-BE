package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "채팅 데이터")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDataRes {

    @Schema(description = "채팅 ID")
    private Long id;
    @Schema(description = "메시지 타입")
    private ChatMessageType messageType;
    @Schema(description = "보낸 사람 ID")
    private Long senderId;
    @Schema(description = "메시지")
    private String message;
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    public static ChatDataRes from(ChatMessage chatMessage) {
        return new ChatDataRes(chatMessage.getId(), chatMessage.getMessageType(),
            chatMessage.getSenderId(), chatMessage.getMessage(), chatMessage.getCreatedAt());
    }
}
