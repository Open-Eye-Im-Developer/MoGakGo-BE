package io.oeid.mogakgo.domain.chat.entity.document;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class ChatMessage {

    private final long id;
    private final long senderId;
    private final String message;
    private final LocalDateTime createdAt;

    @Builder
    private ChatMessage(Long id, Long senderId, String message) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
