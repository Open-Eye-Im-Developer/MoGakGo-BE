package io.oeid.mogakgo.domain.profile.presentation;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ProfileCardPublicSwagger;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.application.ProfileCardService;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileInfoAPIRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/profiles")
public class ProfilePublicController implements ProfileCardPublicSwagger {

    private final ProfileCardService profileCardService;

    @GetMapping("/{region}")
    public ResponseEntity<CursorPaginationResult<UserProfileInfoAPIRes>> getRandomOrderedProfileCardsByRegionPublic(
        @PathVariable Region region, @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok(
            profileCardService.getRandomOrderedProfileCardsByRegionPublic(region, pageable));
    }

}
