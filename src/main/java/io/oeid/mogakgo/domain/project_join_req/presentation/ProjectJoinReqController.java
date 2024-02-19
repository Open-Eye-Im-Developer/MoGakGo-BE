package io.oeid.mogakgo.domain.project_join_req.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.ProjectJoinReqSwagger;
import io.oeid.mogakgo.domain.matching.presentation.MatchingId;
import io.oeid.mogakgo.domain.project_join_req.application.ProjectJoinReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/project-requests")
@RestController
public class ProjectJoinReqController implements ProjectJoinReqSwagger {

    private final ProjectJoinReqService projectJoinReqService;

    @PostMapping("/{projectRequestId}/accept")
    public ResponseEntity<MatchingId> accept(
        @UserId Long userId, @PathVariable Long projectRequestId
    ) {
        return ResponseEntity.ok()
            .body(new MatchingId(projectJoinReqService.accept(userId, projectRequestId)));
    }

}
