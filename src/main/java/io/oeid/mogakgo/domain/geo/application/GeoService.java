package io.oeid.mogakgo.domain.geo.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;

import io.oeid.mogakgo.core.properties.KakaoProperties;
import io.oeid.mogakgo.domain.cert.exception.CertException;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.geo.feign.KakaoFeignClient;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressDocument;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressInfoDto;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeoService {

    private static final String SEPERATOR = " ";
    private final KakaoFeignClient kakaoFeignClient;
    private final KakaoProperties kakaoProperties;
    private final UserCommonService userCommonService;

    public int getUserRegionInfoAboutCoordinates(Long tokenUserId, Long userId, Double x, Double y) {
        User tokenUser = validateToken(tokenUserId);
        validateUserExist(tokenUser, userId);
        return validateCoordinatesCoverage(x, y);
    }

    private int validateCoordinatesCoverage(Double x, Double y) {
        int areaCode = getAreaCodeAboutCoordinates(x, y);
        validateCodeCoverage(areaCode);
        return areaCode;
    }

    private void validateCodeCoverage(int areaCode) {
        if (Region.getByAreaCode(areaCode) == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }

    public int getAreaCodeAboutCoordinates(Double x, Double y) {
        AddressDocument document = getAddressInfoAboutAreaCode(x, y);
        return extractAreaCode(document);
    }

    public AddressDocument getAddressInfoAboutAreaCode(Double x, Double y) {
        String key = generateKey(kakaoProperties);
        AddressInfoDto response = kakaoFeignClient.getAreaCodeAboutCoordinates(key, x, y);
        return response.getDocuments()[0];
    }

    private String generateKey(KakaoProperties kakaoProperties) {
        return kakaoProperties.getPrefix() + SEPERATOR + kakaoProperties.getRestApiKey();
    }

    private int extractAreaCode(AddressDocument document) {
        return Integer.parseInt(document.getCode().substring(0, 5));
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateUserExist(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new CertException(ErrorCode401.CERT_INVALID_INFORMATION);
        }
    }

}
