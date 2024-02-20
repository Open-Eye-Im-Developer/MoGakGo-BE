package io.oeid.mogakgo.domain.matching.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "매칭 ID")
@Getter
public class MatchingId {

    @Schema(description = "매칭 ID", example = "1")
    private final Long matchingId;

    public MatchingId(Long matchingId) {
        this.matchingId = matchingId;
    }

}
