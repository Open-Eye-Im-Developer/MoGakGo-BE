package io.oeid.mogakgo.domain.cert.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.INVALID_CERT_INFORMATION;

import io.oeid.mogakgo.domain.cert.exception.CertException;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.application.UserGeoService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertService {

    private final UserGeoService userGeoService;
    private final UserCommonService userCommonService;

    @Transactional
    public Long certificate(Long tokenUserId, Long userId, int areaCode) {
        User tokenUser = validateToken(tokenUserId);
        validateCertificator(tokenUser, userId);
        Region region = validateAreaCodeCoverage(areaCode);
        if (isPossibleCertification(userId, region)) {
            userGeoService.updateUserGeo(userId, region);
        }
        return userId;
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    // 사용자가 아직 동네 인증을 하지 않았거나, 새롭게 인증하려는 지역이 이미 인증된 지역과 다를 경우만 동네 인증 처리
    private boolean isPossibleCertification(Long userId, Region region) {
        Region userRegionInfo = userGeoService.getUserGeo(userId).getRegion();
        return userRegionInfo == null || userRegionInfo != region;
    }

    private void validateCertificator(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new CertException(INVALID_CERT_INFORMATION);
        }
    }

    private Region validateAreaCodeCoverage(int areaCode) {
        Region region = Region.getByAreaCode(areaCode);
        if (region == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
        return region;
    }

}
