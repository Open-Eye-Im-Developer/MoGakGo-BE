package io.oeid.mogakgo.domain.project_join_req.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ProjectJoinReqSwagger;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingId;
import io.oeid.mogakgo.domain.project_join_req.application.ProjectJoinRequestService;
import io.oeid.mogakgo.domain.project_join_req.application.dto.req.ProjectJoinCreateReq;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestAPIRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.ProjectJoinRequestDetailAPIRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/project-requests")
@RestController
public class ProjectJoinRequestController implements ProjectJoinReqSwagger {

    private final ProjectJoinRequestService projectJoinReqService;

    @PostMapping("/{projectRequestId}/accept")
    public ResponseEntity<MatchingId> accept(
        @UserId Long userId, @PathVariable Long projectRequestId
    ) {
        return ResponseEntity.ok()
            .body(new MatchingId(projectJoinReqService.accept(userId, projectRequestId)));
    }

    @PostMapping
    public ResponseEntity<ProjectJoinRequestAPIRes> create(
        @UserId Long userId, @Valid @RequestBody ProjectJoinCreateReq request
    ) {
        return ResponseEntity.status(201)
            .body(ProjectJoinRequestAPIRes.from(projectJoinReqService.create(userId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursorPaginationResult<ProjectJoinRequestDetailAPIRes>> getBySenderIdWithPagination(
        @UserId Long userId, @PathVariable Long id,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok()
            .body(projectJoinReqService.getBySenderIdWithPagination(userId, id, pageable));
    }
}
