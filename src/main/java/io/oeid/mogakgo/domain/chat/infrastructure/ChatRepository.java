package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
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

    public CursorPaginationResult<ChatDataApiRes> findAllByCollection(String collectionName,
        CursorPaginationInfoReq pageable) {
        Query query = new Query();
        query.limit(pageable.getPageSize()).addCriteria(cursorIdCondition(pageable.getCursorId()))
            .with(Sort.by(Sort.Order.desc("createdAt")));
        var result = mongoTemplate.find(query, ChatMessage.class, collectionName).stream()
            .map(ChatDataApiRes::from).toList();
        return CursorPaginationResult.fromDataWithHasNext(result, pageable.getPageSize(),
            hasNext(pageable.getCursorId(), pageable.getPageSize(), collectionName));
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
