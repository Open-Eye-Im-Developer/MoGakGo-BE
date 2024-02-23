package io.oeid.mogakgo.domain.chat.entity.document;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatMessage {

    private String roomId;
    private String senderId;
    private String message;
    private LocalDateTime createdAt;

    public ChatMessage(String roomId, String senderId, String message) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
