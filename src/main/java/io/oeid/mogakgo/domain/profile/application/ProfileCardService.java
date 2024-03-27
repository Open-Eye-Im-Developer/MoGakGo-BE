package io.oeid.mogakgo.domain.profile.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.profile.application.dto.req.UserProfileCardReq;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.profile.infrastructure.ProfileCardJpaRepository;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileInfoAPIRes;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileCardService {

    private final ProfileCardJpaRepository profileCardRepository;
    private final UserCommonService userCommonService;

    public Long create(UserProfileCardReq request) {

        ProfileCard profileCard = request.toEntity(request.getUser());
        profileCardRepository.save(profileCard);

        return profileCard.getId();
    }

    public CursorPaginationResult<UserProfileInfoAPIRes> getRandomOrderedProfileCardsByRegion(
        Long userId, Region region, CursorPaginationInfoReq pageable
    ) {
        validateToken(userId);
        validateRegionCoverage(region);

        CursorPaginationResult<UserProfileInfoAPIRes> profiles = profileCardRepository
            .findByConditionWithPagination(userId, region, pageable);

        if (profiles.getData().size() >= 2) {
            Collections.shuffle(profiles.getData().subList(1, profiles.getData().size()));
        }
        return profiles;
    }

    public CursorPaginationResult<UserProfileInfoAPIRes> getRandomOrderedProfileCardsByRegionPublic(
        Region region, CursorPaginationInfoReq pageable) {
        validateRegionCoverage(region);
        var result = profileCardRepository.findByConditionWithPaginationPublic(region,
            pageable.getCursorId(), pageable.getPageSize());
        if (result.size() >= 2) {
            Collections.shuffle(result.subList(1, result.size()));
        }
        var profiles = result.stream().map(
            profileCard -> new UserProfileInfoAPIRes(
                UserPublicApiResponse.from(profileCard.getUser()),
                Boolean.FALSE
            )
        ).toList();
        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(profiles,
            pageable.getPageSize());
    }

    private void validateToken(Long userId) {
        userCommonService.getUserById(userId);
    }

    private void validateRegionCoverage(Region region) {
        if (Region.getByAreaCode(region.getAreaCode()) == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }

}
