package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.user.application.dto.res.UserGeoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGeoService {

    private final UserCommonService userCommonService;

    public UserGeoResponse getUserGeo(Long userId) {
        var user = userCommonService.getUserById(userId);
        return new UserGeoResponse(user.getRegion(), user.getRegionAuthenticationAt());
    }

    @Transactional
    public Long updateUserGeo(Long userId, Region region) {
        var user = userCommonService.getUserById(userId);
        user.updateRegion(region);
        return userId;
    }
}
