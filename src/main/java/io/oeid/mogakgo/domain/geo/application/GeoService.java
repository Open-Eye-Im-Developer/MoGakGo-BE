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
        return getRegionAboutCoordinates(x, y).getAreaCode();
    }

    public Region getRegionAboutCoordinates(Double x, Double y) {
        // 해당 법정구역코드가 유효한 서비스 지역인지 검증
        return validateAreaCodeCoverage(getAreaCodeAboutCoordinates(x, y));
    }

    public int getAreaCodeAboutCoordinates(Double x, Double y) {
        // 해당 좌표가 유효한 서비스 지역 내 좌표인지 검증
        validateAvailableCoordinates(x, y);
        AddressDocument document = getAddressInfoAboutAreaCode(x, y);
        return extractAreaCode(document);
    }

    private AddressDocument getAddressInfoAboutAreaCode(Double x, Double y) {
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

    private void validateAvailableCoordinates(Double x, Double y) {
        if (x < 123.0 || x > 132.0 || y < 32.0 || y > 39.0) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
    }

    private Region validateAreaCodeCoverage(int areaCode) {
        Region region = Region.getByAreaCode(areaCode);
        if (region == null) {
            throw new GeoException(INVALID_SERVICE_REGION);
        }
        return region;
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

}
