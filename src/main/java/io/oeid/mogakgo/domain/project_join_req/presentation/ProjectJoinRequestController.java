package io.oeid.mogakgo.domain.project_join_req.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.ProjectJoinRequestSwagger;
import io.oeid.mogakgo.domain.project_join_req.application.ProjectJoinRequestService;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestAPIRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects/join-request")
@RequiredArgsConstructor
public class ProjectJoinRequestController implements ProjectJoinRequestSwagger {

    private final ProjectJoinRequestService projectJoinRequestService;

    @PostMapping
    public ResponseEntity<ProjectJoinRequestAPIRes> create(
        @UserId Long userId, @Valid @RequestBody ProjectJoinCreateReq request
    ) {
        return ResponseEntity.status(201)
            .body(ProjectJoinRequestAPIRes.from(projectJoinRequestService.create(userId, request)));
    }
}
