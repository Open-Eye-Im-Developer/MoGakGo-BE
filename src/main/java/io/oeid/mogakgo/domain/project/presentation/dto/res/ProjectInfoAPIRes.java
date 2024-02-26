package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자가 생성한 프로젝트 리스트 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class ProjectInfoAPIRes {

    @Schema(description = "생성한 프로젝트 ID", example = "11", implementation = Long.class)
    @NotNull
    private final Long projectId;

    @Schema(description = "프로젝트 상태")
    @NotNull
    private final ProjectStatus projectStatus;

    @Schema(description = "프로젝트 생성자 Url")
    @NotNull
    private final String creatorAvatorUrl;

    @Schema(description = "프로젝트 위치 상세")
    @NotNull
    private final String projectLocationDetail;

    @Schema(description = "프로젝트 시작 시간")
    @NotNull
    private final LocalDateTime projectStartTime;

    @Schema(description = "프로젝트 종료 시간")
    @NotNull
    private final LocalDateTime projectEndTime;
}
