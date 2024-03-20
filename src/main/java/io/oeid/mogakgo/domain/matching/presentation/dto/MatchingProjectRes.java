package io.oeid.mogakgo.domain.matching.presentation.dto;

import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import io.oeid.mogakgo.domain.project.domain.entity.ProjectTag;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.presentation.dto.res.MeetingInfoResponse;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자가 매칭 된 프로젝트 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingProjectRes {

    private Long matchingId;
    private ProjectDetailAPIRes response;
    private Long projectRequesterId;

    public MatchingProjectRes(
        Long matchingId, Long id, User creator, ProjectStatus status, List<ProjectTag> tags,
        LocalDateTime projectStartTime, LocalDateTime projectEndTime, String locationDetail,
        Long projectRequesterId
    ) {
        var tagString = tags.stream().map(ProjectTag::getContent).toList();
        this.matchingId = matchingId;
        this.response = ProjectDetailAPIRes.of(
            id, creator, status, tagString,
            new MeetingInfoResponse(projectStartTime, projectEndTime, locationDetail));
        this.projectRequesterId = projectRequesterId;
    }

    public static MatchingProjectRes from(
        Matching matching
    ) {
        if (matching == null) {
            return createNoMatchingRes();
        }
        return new MatchingProjectRes(
            matching.getId(),
            matching.getProject().getId(),
            matching.getProject().getCreator(),
            matching.getProject().getProjectStatus(),
            matching.getProject().getProjectTags(),
            matching.getProject().getMeetingInfo().getMeetStartTime(),
            matching.getProject().getMeetingInfo().getMeetEndTime(),
            matching.getProject().getMeetingInfo().getMeetDetail(),
            matching.getSender().getId()
        );
    }

    public static MatchingProjectRes createNoMatchingRes() {
        return new MatchingProjectRes();
    }
}
