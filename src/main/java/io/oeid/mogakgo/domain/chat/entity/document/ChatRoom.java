package io.oeid.mogakgo.domain.chat.entity.document;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatUserInfo;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chatroom_metadata")
public class ChatRoom {

    @Id
    private Long cursorId;

    private final UUID roomId;

    private final ChatRoomDetail chatRoomDetail;

    private ChatStatus chatStatus;

    private ChatMessage lastMessage;

    @CreatedDate
    private LocalDateTime createdAt;

    private final Map<Long, ChatUserInfo> participants = new HashMap<>();

    private final Map<Long, Boolean> participantsStatus = new HashMap<>();

    private ChatRoom(Long cursorId, UUID roomId, ChatRoomDetail chatRoomDetail) {
        this.cursorId = cursorId;
        this.roomId = roomId;
        this.chatRoomDetail = chatRoomDetail;
        this.chatStatus = ChatStatus.OPEN;
    }

    public static ChatRoom of(Long cursorId, UUID roomId, ChatRoomDetail chatRoomDetail) {
        return new ChatRoom(cursorId, roomId, chatRoomDetail);
    }

    public ChatUserInfo getParticipantUserInfo(Long userId) {
        return Optional.ofNullable(participants.get(userId))
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
    }

    public ChatUserInfo getOpponentUserInfo(Long userId) {
        if(!participants.containsKey(userId)){
            throw new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND);
        }
        return participants.values().stream()
            .filter(userInfo -> !userInfo.userId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
    }

    public void updateLastMessage(ChatMessage message) {
        this.lastMessage = message;
    }

    public void addParticipant(User user) {
        if (participants.size() >= 2) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_MAX_USER_SIZE);
        }
        if (participants.containsKey(user.getId())) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_USER_CANNOT_DUPLICATE);
        }
        participants.put(user.getId(), new ChatUserInfo(user));
        participantsStatus.put(user.getId(), Boolean.TRUE);
    }

    public void closeChatRoom() {
        this.chatStatus = ChatStatus.CLOSED;
    }

    public void leaveChatRoom(Long userId) {
        var status = participantsStatus.get(userId);
        if (status == null || status.equals(Boolean.FALSE)) {
            throw new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND);
        }
        participantsStatus.put(userId, Boolean.FALSE);
        closeChatRoom();
    }

}
