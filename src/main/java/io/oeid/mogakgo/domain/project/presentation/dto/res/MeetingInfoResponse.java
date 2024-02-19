package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Schema(description = "프로젝트 만남 정보 응답 DTO")
@Getter
public class MeetingInfoResponse {

    @Schema(description = "프로젝트 만남 시작 시간", example = "2024-02-19T13:00:00")
    private final LocalDateTime meetStartTime;

    @Schema(description = "프로젝트 만남 종료 시간", example = "2024-02-29T15:00:00")
    private final LocalDateTime meetEndTime;

    @Schema(description = "프로젝트 만남 장소", example = "맥심플랜트 이태원점")
    private final String meetDetail;

    public MeetingInfoResponse(LocalDateTime meetStartTime, LocalDateTime meetEndTime, String meetDetail) {
        this.meetStartTime = meetStartTime;
        this.meetEndTime = meetEndTime;
        this.meetDetail = meetDetail;
    }

    public static MeetingInfoResponse of(MeetingInfo meetingInfo) {
        return new MeetingInfoResponse(
            meetingInfo.getMeetStartTime(),
            meetingInfo.getMeetEndTime(),
            meetingInfo.getMeetDetail()
        );
    }

}
