package io.oeid.mogakgo.domain.project_join_req.domain.entity.enums;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_REQ_STATUS_TO_ACCEPT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_REQ_STATUS_TO_CANCEL;

import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;

public enum RequestStatus {
    PENDING("요청 대기중"),
    ACCEPTED("요청 수락됨"),
    CANCELED("요청 취소됨"),
    REJECTED("요청 거절됨");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public void validateAvailableAccept() {
        if (this != PENDING) {
            throw new ProjectJoinRequestException(INVALID_PROJECT_REQ_STATUS_TO_ACCEPT);
        }
    }

    public void validateAvailableCancel() {
        if (this != PENDING) {
            throw new ProjectJoinRequestException(INVALID_PROJECT_REQ_STATUS_TO_CANCEL);
        }
    }

}
