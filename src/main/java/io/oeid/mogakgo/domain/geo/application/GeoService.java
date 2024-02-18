package io.oeid.mogakgo.domain.geo.application;

import io.oeid.mogakgo.core.properties.KakaoProperties;
import io.oeid.mogakgo.domain.cert.exception.CertException;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.geo.feign.KakaoFeignClient;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressDocument;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressInfoDto;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
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
        return getAreaCodeAboutCoordinates(x, y);
    }

    public int convertAreaCodeToCoordinates(Double x, Double y, int areaCode) {
        return validateCoordinatesCoverage(x, y, areaCode);
    }

    private int validateCoordinatesCoverage(Double x, Double y, int areaCode) {
        int actualCode = getAreaCodeAboutCoordinates(x, y);
        if (actualCode != areaCode) {
            throw new GeoException(ErrorCode400.INVALID_SERVICE_REGION);
        }
        return actualCode;
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
