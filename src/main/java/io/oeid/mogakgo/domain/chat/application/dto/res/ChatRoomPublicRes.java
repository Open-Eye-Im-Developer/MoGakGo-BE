package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfo;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "채팅방 리스트 조회 응답")
@Getter
@AllArgsConstructor
public class ChatRoomPublicRes {
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

    private List<ChatUserInfo> profiles;
}
