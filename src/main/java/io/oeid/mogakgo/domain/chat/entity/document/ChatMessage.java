package io.oeid.mogakgo.domain.chat.entity.document;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatMessage {

    private ChatMessageType messageType;
    private String chatRoomId;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;

    public ChatMessage(ChatMessageType messageType, String chatRoomId, Long senderId, String message) {
        this.messageType = messageType;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}