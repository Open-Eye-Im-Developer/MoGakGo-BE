package io.oeid.mogakgo.domain.project_join_req.presentation.dto.res;

import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자가 보낸 프로젝트 매칭 요청 응답 DTO")
@Getter
public class ProjectJoinRequestDetailAPIRes {

    @Schema(description = "프로젝트 ID", example = "3", implementation = Long.class)
    @NotNull
    private final Long projectId;

    @Schema(description = "프로젝트 생성자 아바타 URL", example = "https://avatars.githubusercontent.com/u/85854384?v=4")
    private final String creatorAvatorUrl;

    @Schema(description = "프로젝트 만남 장ㅔ")
    private final MeetingInfoResponse meetingInfo;

    public ProjectJoinRequestDetailAPIRes(Long projectId, String creatorAvatorUrl,
        MeetingInfoResponse meetingInfo
    ) {
        this.projectId = projectId;
        this.creatorAvatorUrl = creatorAvatorUrl;
        this.meetingInfo = meetingInfo;
    }

    public static ProjectJoinRequestDetailAPIRes from(Long projectId, String creatorAvatorUrl,
        MeetingInfoResponse meetingInfo
    ) {
        return new ProjectJoinRequestDetailAPIRes(projectId, creatorAvatorUrl, meetingInfo);
    }

}
