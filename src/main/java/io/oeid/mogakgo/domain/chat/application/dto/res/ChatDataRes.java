package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDataRes {

    private Long receiverId;
    private Long id;
    private Long senderId;
    private String senderUserName;
    private String message;
    private LocalDateTime createdAt;

    public static ChatDataRes of(ChatRoom chatRoom, ChatMessage chatMessage) {
        var sender = chatRoom.getParticipantUserInfo(chatMessage.getSenderId());
        var receiver = chatRoom.getOpponentUserInfo(chatMessage.getSenderId());
        return new ChatDataRes(
            receiver.userId(),
            chatMessage.getId(),
            chatMessage.getSenderId(),
            sender.username(),
            chatMessage.getMessage(),
            chatMessage.getCreatedAt());
    }

    public ChatDataApiRes toApiResponse() {
        return ChatDataApiRes.of(id, senderId, message, createdAt);
    }
}
