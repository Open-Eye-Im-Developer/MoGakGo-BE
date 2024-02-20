package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Schema(description = "지역별 프로젝트 밀도 순위")
@Getter
public class ProjectDensityRankRes {

    @Schema(description = "지역별 프로젝트 밀도 순위. 밀도가 높은 순서대로 정렬됨. 인덱스 0부터 1위로 시작.",
        example = "[\"JONGRO\", \"JUNG\", \"YONGSAN\"]")
    private List<Region> densityRankByRegion;

    public ProjectDensityRankRes(List<Region> densityRankByRegion) {
        this.densityRankByRegion = densityRankByRegion;
    }
}
