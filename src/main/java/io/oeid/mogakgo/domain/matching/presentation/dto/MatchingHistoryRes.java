package io.oeid.mogakgo.domain.matching.presentation.dto;

import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingHistoryRes {

    private final Long matchingId;
    private final MatchingStatus status;
    private final String anotherUserAvatarUrl;
    private final String projectLocationDetail;
    private final LocalDateTime projectStartTime;
    private final LocalDateTime projectEndTime;

}
