package io.oeid.mogakgo.domain.chat.application.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "채팅방 프로젝트 정보")
@Getter
@AllArgsConstructor
public class ChatRoomProjectInfo {

    @Schema(description = "프로젝트 설명")
    private String meetDetail;
    @Schema(description = "프로젝트 시작 시간")
    private LocalDateTime meetStartTime;
    @Schema(description = "프로젝트 종료 시간")
    private LocalDateTime meetEndTime;
}
