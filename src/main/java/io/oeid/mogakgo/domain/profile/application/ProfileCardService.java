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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileCardService {

    private final ProfileCardJpaRepository profileCardRepository;
    private final UserCommonService userCommonService;

    public CursorPaginationResult<UserPublicApiResponse> getRandomOrderedProfileCardsByRegion(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {
        validateToken(userId);
        validateRegionCoverage(region);

        CursorPaginationResult<UserPublicApiResponse> profiles = profileCardRepository
            .findByConditionWithPagination(
            null, region, pageable
        );

        List<UserPublicApiResponse> shuffledData = new ArrayList<>(profiles.getData());
        Collections.shuffle(shuffledData);

        profiles.setData(shuffledData);
        return profiles;
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
