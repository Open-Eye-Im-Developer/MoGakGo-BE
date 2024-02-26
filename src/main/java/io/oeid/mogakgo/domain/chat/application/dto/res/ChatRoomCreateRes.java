package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import lombok.Getter;

@Getter
public class ChatRoomCreateRes {

    private String roomId;
    private Long projectId;
    private Long creatorId;
    private Long senderId;
    private ChatStatus chatStatus;

    private ChatRoomCreateRes(String roomId, Long projectId, Long creatorId, Long senderId,
        ChatStatus chatStatus) {
        this.roomId = roomId;
        this.projectId = projectId;
        this.creatorId = creatorId;
        this.senderId = senderId;
        this.chatStatus = chatStatus;
    }

    public static ChatRoomCreateRes from(ChatRoom chatRoom) {
        return new ChatRoomCreateRes(chatRoom.getId(), chatRoom.getProject().getId(),
            chatRoom.getCreator().getId(), chatRoom.getSender().getId(), chatRoom.getStatus());
    }

}
