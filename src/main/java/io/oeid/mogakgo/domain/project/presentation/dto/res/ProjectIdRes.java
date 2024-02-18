package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "프로젝트 카드 생성 응답. 생성된 프로젝트의 ID를 반환한다.")
@Getter
public class ProjectIdRes {

    @Schema(description = "생성 된 프로젝트 ID", example = "1")
    private final Long id;

    private ProjectIdRes(Long id) {
        this.id = id;
    }

    public static ProjectIdRes from(Long id) {
        return new ProjectIdRes(id);
    }

}
