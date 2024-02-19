package io.oeid.mogakgo.domain.project_join_req.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "프로젝트 매칭 생성 요청 DTO")
@Getter
public class ProjectJoinRequestAPIRes {

    @Schema(description = "생성된 프로젝트 매칭 ID", example = "1", implementation = Long.class)
    private final Long id;

    private ProjectJoinRequestAPIRes(Long id) {
        this.id = id;
    }

    public static ProjectJoinRequestAPIRes from(Long id) {
        return new ProjectJoinRequestAPIRes(id);
    }

}
