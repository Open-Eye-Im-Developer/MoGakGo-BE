package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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

    public List<ChatMessage> findAllByCollection(String collectionName,
        Long cursorId, int pageSize) {
        Query query = new Query();
        query.limit(pageSize + 1).addCriteria(cursorIdCondition(cursorId))
            .with(Sort.by(Sort.Order.desc("createdAt")));
        return mongoTemplate.find(query, ChatMessage.class, collectionName);
    }

    public Optional<ChatMessage> findLastChatByCollection(String collectionName) {
        Query query = new Query();
        query.limit(1).with(Sort.by(Sort.Order.desc("createdAt")));
        return Optional.ofNullable(mongoTemplate.findOne(query, ChatMessage.class, collectionName));
    }

    private Criteria cursorIdCondition(Long cursorId) {
        return cursorId != null ? Criteria.where("id").lt(cursorId) : Criteria.where("id").gt(0L);
    }

    private boolean hasNext(Long cursorId, int pageSize, String collectionName) {
        return cursorId == null ? mongoTemplate.estimatedCount(collectionName) > pageSize
            : mongoTemplate.count(new Query(Criteria.where("id").lt(cursorId)), ChatMessage.class,
                collectionName) > pageSize;
    }
}
