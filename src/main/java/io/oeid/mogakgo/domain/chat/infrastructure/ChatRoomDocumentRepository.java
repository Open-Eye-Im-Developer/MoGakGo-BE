package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomDocumentRepository {

    private final MongoTemplate mongoTemplate;

    public ChatRoom save(ChatRoom chatRoom) {
        return mongoTemplate.save(chatRoom);
    }

    public Optional<ChatRoom> findByRoomIdAndUserId(@NonNull UUID roomId, @NonNull Long userId) {
        Query query = new Query();
        query
            .addCriteria(verifyRoomId(roomId))
            .addCriteria(verifyUserId(userId));
        return Optional.ofNullable(mongoTemplate.findOne(query, ChatRoom.class));
    }

    public List<ChatRoom> findChatRoomsByUserId(Long userId, Long cursorId, int pageSize) {
        Query query = new Query();
        query.limit(pageSize + 1).addCriteria(verifyUserId(userId));
        if (cursorId != null) {
            query.addCriteria(Criteria.where("cursorId").lt(cursorId));
        }
        return mongoTemplate.find(query, ChatRoom.class);
    }

    public Optional<ChatRoom> findByProjectId(@NonNull Long projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomDetail.projectId").is(projectId));
        return Optional.ofNullable(mongoTemplate.findOne(query, ChatRoom.class));
    }

    private Criteria verifyRoomId(UUID roomId) {
        return Criteria.where("roomId").is(roomId);
    }

    private Criteria verifyUserId(Long userId) {
        return Criteria.where("participantsStatus." + userId).is(Boolean.TRUE);
    }

}
