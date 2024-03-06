package io.oeid.mogakgo.domain.chat.entity.document;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class ChatMessage {

    @Setter
    private Long id;
    private ChatMessageType messageType;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;

    @Builder
    private ChatMessage(Long id, ChatMessageType messageType, Long senderId, String message) {
        this.id = id;
        this.messageType = messageType;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
