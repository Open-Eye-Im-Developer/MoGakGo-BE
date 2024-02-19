package io.oeid.mogakgo.domain.project.domain.entity.enums;

import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_CANCEL_NOT_ALLOWED;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROJECT_DELETION_NOT_ALLOWED;

import io.oeid.mogakgo.domain.project.exception.ProjectException;
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
}
