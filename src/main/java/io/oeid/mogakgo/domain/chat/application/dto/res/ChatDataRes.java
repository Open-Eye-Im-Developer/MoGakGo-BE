package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDataRes {

    private Long receiverId;
    private Long id;
    private ChatMessageType messageType;
    private Long senderId;
    private String senderUserName;
    private String message;
    private LocalDateTime createdAt;

    public static ChatDataRes of(Long receiverId, String senderUserName,  ChatMessage chatMessage) {
        return new ChatDataRes(
            receiverId,
            chatMessage.getId(),
            chatMessage.getMessageType(),
            chatMessage.getSenderId(),
            senderUserName,
            chatMessage.getMessage(),
            chatMessage.getCreatedAt());
    }

    public ChatDataApiRes toApiResponse() {
        return ChatDataApiRes.of(id, messageType, senderId, message, createdAt);
    }
}
