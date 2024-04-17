package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Schema(description = "채팅방 리스트 조회 응답")
@Getter
public class ChatRoomRes {

    @Schema(description = "커서 ID")
    private Long cursorId;
    @Schema(description = "프로젝트 ID")
    private String roomId;
    @Schema(description = "프로젝트 진행 장소")
    private String meetDetail;
    @Schema(description = "프로젝트 진행 시작 시간")
    private LocalDateTime meetStartTime;
    @Schema(description = "마지막 메시지")
    private String lastMessage;
    @Schema(description = "마지막 메시지 생성 시간")
    private LocalDateTime lastMessageCreatedAt;
    @Schema(description = "채팅방 상태")
    private String chatStatus;

    public ChatRoomRes(Long cursorId, String roomId, String meetDetail, LocalDateTime meetStartTime,
        String chatStatus) {
        this.cursorId = cursorId;
        this.roomId = roomId;
        this.meetDetail = meetDetail;
        this.meetStartTime = meetStartTime;
        this.chatStatus = chatStatus;
    }

    private void addLastMessage(String lastMessage, LocalDateTime lastMessageCreatedAt) {
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }

    public static ChatRoomRes from(ChatRoom chatRoom) {
        var chatRoomRes = new ChatRoomRes(
            chatRoom.getCursorId(),
            chatRoom.getRoomId().toString(),
            chatRoom.getChatRoomDetail().getMeetDetail(),
            chatRoom.getChatRoomDetail().getMeetStartTime(),
            chatRoom.getChatStatus().name()
        );
        var lastMessage = chatRoom.getLastMessage();
        if (lastMessage != null) {
            chatRoomRes.addLastMessage(lastMessage.getMessage(), lastMessage.getCreatedAt());
        }
        return chatRoomRes;
    }
}
