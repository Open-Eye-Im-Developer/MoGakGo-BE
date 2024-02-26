package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Schema(description = "프로젝트 카드 조회 리스트 응답 DTO")
@Getter
public class ProjectDetailAPIRes {

    @Schema(description = "프로젝트 ID", example = "0", implementation = Long.class)
    private final Long projectId;

    @Schema(description = "프로젝트 생성자 정보")
    private final UserPublicApiResponse creator;

    @Schema(description = "프로젝트 상태")
    private final ProjectStatus projectStatus;

    @Schema(description = "프로젝트 모임 태그", example = "[\"수다스러운\", \"재밌는\"]")
    private final List<String> projectTags;

    @Schema(description = "프로젝트 만남 장소 정보")
    private final MeetingInfoResponse meetingInfo;


    public ProjectDetailAPIRes(Long projectId, UserPublicApiResponse creator,
        ProjectStatus projectStatus, List<String> projectTags, MeetingInfoResponse meetingInfo
    ) {
        this.projectId = projectId;
        this.creator = creator;
        this.projectStatus = projectStatus;
        this.projectTags = projectTags;
        this.meetingInfo = meetingInfo;
    }

    public static ProjectDetailAPIRes of(Long projectId, UserPublicApiResponse creator,
        ProjectStatus projectStatus, List<String> projectTags, MeetingInfoResponse meetingInfo) {
        return new ProjectDetailAPIRes(
            projectId,
            creator,
            projectStatus,
            projectTags,
            meetingInfo
        );
    }
}
