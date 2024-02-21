package io.oeid.mogakgo.domain.project_join_req.application.dto.req;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "프로젝트 매칭 요청 생성")
@Getter
public class ProjectJoinCreateReq {

    @Schema(description = "프로젝트 매칭 요청자 ID", example = "2", implementation = Long.class)
    @NotNull
    private final Long senderId;

    @Schema(description = "매칭이 진행될 프로젝트 ID", example = "1", implementation = Long.class)
    @NotNull
    private final Long projectId;

    public ProjectJoinCreateReq(Long senderId, Long projectId) {
        this.senderId = senderId;
        this.projectId = projectId;
    }

    public ProjectJoinRequest toEntity(User sender, Project project) {
        return ProjectJoinRequest.of(sender, project);
    }

}
