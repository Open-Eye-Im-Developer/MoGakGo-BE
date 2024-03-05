package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfo;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Schema(description = "채팅방 리스트 조회 응답")
@Getter
public class ChatRoomPublicRes {

    @Schema(description = "커서 ID")
    private Long cursorId;
    @Schema(description = "프로젝트 ID")
    private Long projectId;
    @Schema(description = "채팅방 ID")
    private String chatRoomId;
    @Schema(description = "마지막 메시지")
    private String lastMessage;
    @Schema(description = "마지막 메시지 생성 시간")
    private LocalDateTime lastMessageCreatedAt;
    @Schema(description = "채팅방 상태")
    private ChatStatus status;

    private ChatUserInfo chatUserInfo;

    private ChatRoomPublicRes(Long cursorId, Long projectId, UUID chatRoomId, ChatStatus status,
        ChatUserInfo chatUserInfo) {
        this.cursorId = cursorId;
        this.projectId = projectId;
        this.chatRoomId = chatRoomId.toString();
        this.status = status;
        this.chatUserInfo = chatUserInfo;
    }

    public static ChatRoomPublicRes of(ChatRoom chatRoom, User user) {
        return new ChatRoomPublicRes(
            chatRoom.getCursorId(),
            chatRoom.getProject().getId(),
            chatRoom.getId(),
            chatRoom.getStatus(),
            ChatUserInfo.from(user)
        );
    }

    public void addLastMessage(String lastMessage, LocalDateTime lastMessageCreatedAt) {
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }
}
