package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅방 프로젝트 정보")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChatRoomDataRes {

    @Schema(description = "프로젝트 설명")
    private String meetDetail;
    @Schema(description = "프로젝트 시작 시간")
    private LocalDateTime meetStartTime;
    @Schema(description = "프로젝트 종료 시간")
    private LocalDateTime meetEndTime;

    public static ChatRoomDataRes from(MeetingInfo meetingInfo) {
        return new ChatRoomDataRes(meetingInfo.getMeetDetail(), meetingInfo.getMeetStartTime(),
            meetingInfo.getMeetEndTime());
    }
}
