package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자가 생성한 최근 프로젝트 조회 응답 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetailInfoAPIRes {

    private final Long matchingId;
    private final List<ProjectDetailAPIRes> response;

    public static ProjectDetailInfoAPIRes of(Long matchingId, List<ProjectDetailAPIRes> response) {
        return new ProjectDetailInfoAPIRes(matchingId, response);
    }
}
