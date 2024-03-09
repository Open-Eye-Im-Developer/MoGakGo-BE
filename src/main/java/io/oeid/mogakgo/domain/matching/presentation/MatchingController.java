package io.oeid.mogakgo.domain.matching.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.MatchingSwagger;
import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingHistoryRes;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/matches")
@RestController
public class MatchingController implements MatchingSwagger {

    private final MatchingService matchingService;

    @PostMapping("/{matchingId}/cancel")
    public ResponseEntity<MatchingId> cancel(@UserId Long userId, @PathVariable Long matchingId) {
        return ResponseEntity.ok(new MatchingId(matchingService.cancel(userId, matchingId)));
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<CursorPaginationResult<MatchingHistoryRes>> getMyMatches(
        @UserId Long tokenId, @PathVariable Long userId,
        @RequestParam(required = false) MatchingStatus status,
        @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok(
            matchingService.getMyMatches(tokenId, userId, status, pageable));
    }

}
