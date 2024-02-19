package io.oeid.mogakgo.domain.profile.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.profile.infrastructure.ProfileCardJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileCardService {

    private final ProfileCardJpaRepository profileCardRepository;
    private final UserCommonService userCommonService;

    public CursorPaginationResult<UserPublicApiResponse> getRandomOrderedProfileCardsByRegion(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {
        validateToken(userId);
        validateRegionCoverage(region);

        CursorPaginationResult<UserPublicApiResponse> projects = profileCardRepository
            .findByConditionWithPagination(
            null, region, pageable
        );
        Collections.shuffle(projects.getData());
        return projects;
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateRegionCoverage(Region region) {
        if (Region.getByAreaCode(region.getAreaCode()) == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }

}
