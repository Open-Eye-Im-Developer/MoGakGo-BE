package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfoRes;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatUserInfo;
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

    private ChatUserInfoRes chatUserInfoRes;

    public ChatRoomDataRes(ChatRoomDetail chatRoomDetail, ChatUserInfo chatUserInfo) {
        this.meetDetail = chatRoomDetail.getMeetDetail();
        this.meetStartTime = chatRoomDetail.getMeetStartTime();
        this.meetLocationLatitude = chatRoomDetail.getMeetLocationLatitude();
        this.meetLocationLongitude = chatRoomDetail.getMeetLocationLongitude();
        this.meetEndTime = chatRoomDetail.getMeetEndTime();
        this.chatUserInfoRes = ChatUserInfoRes.from(chatUserInfo);
    }

}
