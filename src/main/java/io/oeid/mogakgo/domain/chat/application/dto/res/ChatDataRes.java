package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import io.oeid.mogakgo.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDataRes {

    private Long receiverId;
    private String receiverUsername;
    private Long id;
    private ChatMessageType messageType;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;

    public static ChatDataRes of(User receiver, ChatMessage chatMessage) {
        return new ChatDataRes(receiver.getId(), receiver.getUsername(), chatMessage.getId(),
            chatMessage.getMessageType(), chatMessage.getSenderId(), chatMessage.getMessage(),
            chatMessage.getCreatedAt());
    }

    public ChatDataApiRes toApiResponse() {
        return ChatDataApiRes.of(id, messageType, senderId, message, createdAt);
    }
}
