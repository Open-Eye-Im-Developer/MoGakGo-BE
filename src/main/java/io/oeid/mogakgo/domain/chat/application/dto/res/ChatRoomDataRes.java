package io.oeid.mogakgo.domain.chat.application.dto.res;

import io.oeid.mogakgo.domain.chat.application.vo.ChatData;
import io.oeid.mogakgo.domain.chat.application.vo.ChatRoomProjectInfo;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅방 데이터 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChatRoomDataRes {

    private ChatRoomProjectInfo project;
    private List<ChatData> data;

    public static ChatRoomDataRes of(MeetingInfo meetingInfo, List<ChatMessage> data) {
        ChatRoomProjectInfo project = new ChatRoomProjectInfo(meetingInfo.getMeetDetail(),
            meetingInfo.getMeetStartTime(), meetingInfo.getMeetEndTime());
        return new ChatRoomDataRes(project, data.stream().map(ChatData::from).toList());
    }
}
