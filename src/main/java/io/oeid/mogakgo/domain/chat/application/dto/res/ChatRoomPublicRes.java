package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
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
    @Schema(description = "미팅 상세 정보")
    private String meetDetail;
    @Schema(description = "미팅 시작 시간")
    private LocalDateTime meetStartTime;
    @Schema(description = "미팅 종료 시간")
    private LocalDateTime meetEndTime;
    @Schema(description = "채팅방 ID")
    private String chatRoomId;
    @Schema(description = "마지막 메시지")
    private String lastMessage;
    @Schema(description = "마지막 메시지 생성 시간")
    private LocalDateTime lastMessageCreatedAt;
    @Schema(description = "채팅방 상태")
    private ChatStatus status;


    public ChatRoomPublicRes(Long cursorId, Long projectId, UUID chatRoomId, ChatStatus status,
        String meetDetail, LocalDateTime meetStartTime, LocalDateTime meetEndTime) {
        this.cursorId = cursorId;
        this.projectId = projectId;
        this.chatRoomId = chatRoomId.toString();
        this.status = status;
        this.meetDetail = meetDetail;
        this.meetStartTime = meetStartTime;
        this.meetEndTime = meetEndTime;
    }

    public void addLastMessage(String lastMessage, LocalDateTime lastMessageCreatedAt) {
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }
}
