package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final MongoTemplate mongoTemplate;

    public void createCollection(String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }
    }

    public ChatMessage save(ChatMessage chatMessage, String collectionName) {
        return mongoTemplate.save(chatMessage, collectionName);
    }

    public List<ChatMessage> findAllByCollection(String collectionName) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("createdAt")));
        return mongoTemplate.find(query, ChatMessage.class, collectionName);
    }
}
