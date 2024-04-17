package io.oeid.mogakgo.domain.chat.entity.vo;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatRoomDetail {

    private final Long projectId;
    private final String meetDetail;
    private final Double meetLocationLatitude;
    private final Double meetLocationLongitude;
    private final LocalDateTime meetStartTime;
    private final LocalDateTime meetEndTime;

    private ChatRoomDetail(Long projectId, MeetingInfo meetingInfo) {
        var meetLocation = meetingInfo.getMeetLocation();
        this.projectId = projectId;
        this.meetDetail = meetingInfo.getMeetDetail();
        this.meetLocationLatitude = meetLocation.getX();
        this.meetLocationLongitude = meetLocation.getY();
        this.meetStartTime = meetingInfo.getMeetStartTime();
        this.meetEndTime = meetingInfo.getMeetEndTime();
    }

    public static ChatRoomDetail from(Project project) {
        var meetingInfo = project.getMeetingInfo();
        return new ChatRoomDetail(project.getId(), meetingInfo);
    }
}
