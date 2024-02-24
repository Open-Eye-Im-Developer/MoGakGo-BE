package io.oeid.mogakgo.domain.cert.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.INVALID_CERT_INFORMATION;

import io.oeid.mogakgo.domain.cert.exception.CertException;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertService {

    private final UserCommonService userCommonService;

    @Transactional
    public Long certificate(Long tokenUserId, Long userId, int areaCode) {
        User user = validateToken(tokenUserId);
        validateCertificator(user, userId);
        Region region = validateAreaCodeCoverage(areaCode);

        user.updateRegion(region);
        return userId;
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
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
