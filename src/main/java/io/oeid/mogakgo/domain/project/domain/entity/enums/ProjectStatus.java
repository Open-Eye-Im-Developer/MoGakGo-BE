package io.oeid.mogakgo.domain.project.domain.entity.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    PENDING("매칭 대기중"),
    MATCHED("매칭 완료됨"),
    CANCELED("매칭 취소됨");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }
}
