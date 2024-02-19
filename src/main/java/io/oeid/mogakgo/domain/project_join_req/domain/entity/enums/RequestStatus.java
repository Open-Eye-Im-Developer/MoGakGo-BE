package io.oeid.mogakgo.domain.project_join_req.domain.entity.enums;

public enum RequestStatus {
    PENDING("요청 대기중"),
    ACCEPTED("요청 수락됨"),
    REJECTED("요청 거절됨");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

}
