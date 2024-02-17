package io.oeid.mogakgo.domain.project.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로젝트 태그 생성 요청")
@Getter
@NoArgsConstructor
public class ProjectTagCreateReq {

    @Schema(description = "태그 내용", example = "인싸", implementation = String.class)
    @NotNull
    @Size(min = 1, max = 7)
    private String content;

}
