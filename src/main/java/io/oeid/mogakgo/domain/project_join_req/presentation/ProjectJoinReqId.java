package io.oeid.mogakgo.domain.project_join_req.presentation;

import lombok.Getter;

@Getter
public class ProjectJoinReqId {

    private final Long projectRequestId;

    public ProjectJoinReqId(Long projectRequestId) {
        this.projectRequestId = projectRequestId;
    }

}
