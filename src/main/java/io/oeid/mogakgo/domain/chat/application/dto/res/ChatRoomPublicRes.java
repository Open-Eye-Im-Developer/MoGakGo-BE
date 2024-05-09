package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfoRes;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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

    private final ChatUserInfoRes chatUserInfoRes;

    private ChatRoomPublicRes(Long cursorId, Long projectId, String chatRoomId, ChatStatus status,
        ChatUserInfoRes chatUserInfoRes) {
        this.status = status;
        this.chatRoomId = chatRoomId;
        this.cursorId = cursorId;
        this.projectId = projectId;
        this.chatUserInfoRes = chatUserInfoRes;
    }

    private void addLastMessage(String lastMessage, LocalDateTime lastMessageCreatedAt) {
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }

    public static ChatRoomPublicRes of(ChatRoom chatRoom, Long userId) {
        var chatUserInfo = chatRoom.getOpponentUserInfo(userId);
        var chatRoomRes = new ChatRoomPublicRes(
            chatRoom.getCursorId(),
            chatRoom.getChatRoomDetail().getProjectId(),
            chatRoom.getRoomId().toString(),
            chatRoom.getChatStatus(),
            ChatUserInfoRes.from(chatUserInfo)
        );
        var lastMessage = chatRoom.getLastMessage();
        if (lastMessage != null) {
            chatRoomRes.addLastMessage(lastMessage.getMessage(), lastMessage.getCreatedAt());
        }
        return chatRoomRes;
    }
}
