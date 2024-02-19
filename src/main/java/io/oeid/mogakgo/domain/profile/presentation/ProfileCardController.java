package io.oeid.mogakgo.domain.profile.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ProfileCardSwagger;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.application.ProfileCardService;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileCardController implements ProfileCardSwagger {

    private final ProfileCardService profileCardService;

    @GetMapping("/{region}")
    public ResponseEntity<CursorPaginationResult<UserPublicApiResponse>> getRandomOrderedProfileCardsByRegion(
        @UserId Long userId, @PathVariable Region region,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok().body(
            profileCardService.getRandomOrderedProfileCardsByRegion(userId, region, pageable));
    }
}
