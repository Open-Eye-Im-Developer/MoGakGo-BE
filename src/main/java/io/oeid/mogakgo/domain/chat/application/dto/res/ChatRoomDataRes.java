package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfo;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅방 프로젝트 정보")
@Getter
@NoArgsConstructor
public class ChatRoomDataRes {

    @Schema(description = "프로젝트 설명")
    private String meetDetail;
    @Schema(description = "프로젝트 시작 시간")
    private LocalDateTime meetStartTime;
    @Schema(description = "프로젝트 위치 위도")
    private Double meetLocationLatitude;
    @Schema(description = "프로젝트 위치 경도")
    private Double meetLocationLongitude;
    @Schema(description = "프로젝트 종료 시간")
    private LocalDateTime meetEndTime;

    private ChatUserInfo chatUserInfo;

    public ChatRoomDataRes(MeetingInfo meetingInfo, ChatUserInfo chatUserInfo) {
        var meetLocation = meetingInfo.getMeetLocation();
        this.meetDetail = meetingInfo.getMeetDetail();
        this.meetStartTime = meetingInfo.getMeetStartTime();
        this.meetLocationLatitude = meetLocation.getX();
        this.meetLocationLongitude = meetLocation.getY();
        this.meetEndTime = meetingInfo.getMeetEndTime();
        this.chatUserInfo = chatUserInfo;
    }

}
