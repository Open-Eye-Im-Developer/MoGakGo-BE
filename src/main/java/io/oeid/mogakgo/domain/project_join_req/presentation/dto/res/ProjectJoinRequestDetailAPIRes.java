package io.oeid.mogakgo.domain.project_join_req.presentation.dto.res;

import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자가 보낸 프로젝트 매칭 요청 응답 DTO")
@Getter
public class ProjectJoinRequestDetailAPIRes {

    @Schema(description = "프로젝트 요청 ID", example = "10", implementation = Long.class)
    @NotNull
    private final Long id;

    @Schema(description = "프로젝트 ID", example = "3", implementation = Long.class)
    @NotNull
    private final Long projectId;

    @Schema(description = "프로젝트 생성자 아바타 URL", example = "https://avatars.githubusercontent.com/u/85854384?v=4")
    private final String creatorAvatorUrl;

    @Schema(description = "프로젝트 만남 장소")
    private final MeetingInfoResponse meetingInfo;

    public ProjectJoinRequestDetailAPIRes(Long id, Long projectId,
        String creatorAvatorUrl, MeetingInfoResponse meetingInfo
    ) {
        this.id = id;
        this.projectId = projectId;
        this.creatorAvatorUrl = creatorAvatorUrl;
        this.meetingInfo = meetingInfo;
    }

    public static ProjectJoinRequestDetailAPIRes from(Long id, Long projectId,
        String creatorAvatorUrl, MeetingInfoResponse meetingInfo
    ) {
        return new ProjectJoinRequestDetailAPIRes(
            id, projectId, creatorAvatorUrl, meetingInfo);
    }

}
