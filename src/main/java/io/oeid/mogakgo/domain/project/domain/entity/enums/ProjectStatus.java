package io.oeid.mogakgo.domain.project.domain.entity.enums;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_STATUS_TO_FINISH;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_CANCEL_NOT_ALLOWED;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_DELETION_NOT_ALLOWED;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_STATUS_TO_ACCEPT;

import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import lombok.Getter;

@Getter
public enum ProjectStatus {
    PENDING("매칭 대기중"),
    MATCHED("매칭 완료됨"),
    CANCELED("매칭 취소됨"),
    FINISHED("매칭 종료됨");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public void validateAvailableDelete() {
        if (this == PENDING || this == MATCHED) {
            throw new ProjectException(PROJECT_DELETION_NOT_ALLOWED);
        }
    }

    public void validateAvailableCancel() {
        if (this == CANCELED || this == FINISHED) {
            throw new ProjectException(PROJECT_CANCEL_NOT_ALLOWED);
        }
    }

    public void validateAvailableMatched() {
        if (this != PENDING) {
            throw new ProjectJoinRequestException(INVALID_PROJECT_STATUS_TO_ACCEPT);
        }
    }

    public void validateAvailableFinish() {
        if (this != MATCHED) {
            throw new ProjectException(INVALID_PROJECT_STATUS_TO_FINISH);
        }
    }
}
