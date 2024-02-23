package io.oeid.mogakgo.domain.geo.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_SERVICE_REGION;

import io.oeid.mogakgo.core.properties.KakaoProperties;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.geo.exception.GeoException;
import io.oeid.mogakgo.domain.geo.application.feign.KakaoFeignClient;
import io.oeid.mogakgo.domain.geo.application.feign.dto.AddressDocument;
import io.oeid.mogakgo.domain.geo.application.feign.dto.AddressInfoDto;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GeoService {

    private static final String SEPERATOR = " ";
    private final KakaoFeignClient kakaoFeignClient;
    private final KakaoProperties kakaoProperties;
    private final UserCommonService userCommonService;

    public int getUserRegionInfoAboutCoordinates(Long tokenUserId, Double x, Double y) {
        validateToken(tokenUserId);
        validateCoordinates(x, y);
        return validateCoordinatesCoverage(x, y);
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

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateCoordinates(Double x, Double y) {
        if (x < 123.0 || x > 132.0 || y < 32.0 || y > 39.0) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }

}
