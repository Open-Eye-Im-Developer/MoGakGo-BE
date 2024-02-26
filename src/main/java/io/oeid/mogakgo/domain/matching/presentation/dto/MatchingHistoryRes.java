package io.oeid.mogakgo.domain.matching.presentation.dto;

import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "매칭 이력")
@Getter
@AllArgsConstructor
public class MatchingHistoryRes {

    @Schema(description = "매칭 ID")
    private final Long matchingId;
    @Schema(description = "매칭 상태")
    private final MatchingStatus status;
    @Schema(description = "상대방 프로필 이미지 URL")
    private final String anotherUserAvatarUrl;
    @Schema(description = "프로젝트 위치 상세")
    private final String projectLocationDetail;
    @Schema(description = "프로젝트 시작 시간")
    private final LocalDateTime projectStartTime;
    @Schema(description = "프로젝트 종료 시간")
    private final LocalDateTime projectEndTime;

}
