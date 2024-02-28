package io.oeid.mogakgo.domain.chat.entity.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "chat_sequence")
public class ChatIdSequence {
    @Id
    private String id;
    private Long seq;
}
