package io.oeid.mogakgo.domain.matching.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.MatchingSwagger;
import io.oeid.mogakgo.domain.matching.application.MatchingService;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
