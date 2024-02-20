package io.oeid.mogakgo.domain.project.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ProjectSwagger;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.application.ProjectService;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectDetailAPIRes;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectCreateReq;
import io.oeid.mogakgo.domain.project.presentation.dto.res.ProjectIdRes;
import io.oeid.mogakgo.domain.project_join_req.presentation.dto.res.projectJoinRequestRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController implements ProjectSwagger {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectIdRes> create(
        @UserId Long userId, @Valid @RequestBody ProjectCreateReq request
    ) {
        return ResponseEntity.status(201)
            .body(ProjectIdRes.from(projectService.create(userId, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @UserId Long userId, @PathVariable Long id
    ) {
        projectService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ProjectIdRes> cancel(
        @UserId Long userId, @PathVariable Long id
    ) {
        projectService.cancel(userId, id);
        return ResponseEntity.status(200).body(ProjectIdRes.from(id));
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<CursorPaginationResult<projectJoinRequestRes>> getJoinRequest(
        @UserId Long userId, @PathVariable Long id,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok().body(projectService.getJoinRequest(userId, id, pageable));
    }

    @GetMapping("/{region}")
    public ResponseEntity<CursorPaginationResult<ProjectDetailAPIRes>> getRandomOrderedProjectsByRegion(
        @UserId Long userId, @PathVariable Region region,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok().body(
            projectService.getRandomOrderedProjectsByRegion(userId, region, pageable)
        );
    }

}
