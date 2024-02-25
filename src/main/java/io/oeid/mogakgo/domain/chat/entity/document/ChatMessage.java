package io.oeid.mogakgo.domain.chat.entity.document;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatMessage {

    private ChatMessageType messageType;
    private String roomId;
    private String senderId;
    private String message;
    private LocalDateTime createdAt;

    public ChatMessage(ChatMessageType messageType, String roomId, String senderId, String message) {
        this.messageType = messageType;
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
