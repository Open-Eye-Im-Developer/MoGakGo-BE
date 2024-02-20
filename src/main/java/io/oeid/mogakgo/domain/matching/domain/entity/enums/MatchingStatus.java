package io.oeid.mogakgo.domain.matching.domain.entity.enums;

import static io.oeid.mogakgo.exception.code.ErrorCode400.MATCHING_CANCEL_NOT_ALLOWED;

import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import lombok.Getter;

@Getter
public enum MatchingStatus {
    PROGRESS("매칭 진행중"),
    CANCELED("매칭 취소됨"),
    FINISHED("매칭 종료됨")
    ;

    private final String description;

    MatchingStatus(String description) {
        this.description = description;
    }

    public void validateAvailableCancel() {
        if (this != PROGRESS) {
            throw new MatchingException(MATCHING_CANCEL_NOT_ALLOWED);
        }
    }
}
