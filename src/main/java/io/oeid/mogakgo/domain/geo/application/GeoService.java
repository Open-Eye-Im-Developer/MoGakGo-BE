package io.oeid.mogakgo.domain.geo.application;

import io.oeid.mogakgo.core.properties.KakaoProperties;
import io.oeid.mogakgo.domain.geo.feign.KakaoFeignClient;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressDocument;
import io.oeid.mogakgo.domain.geo.feign.dto.AddressInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeoService {

    private static final String SEPERATOR = " ";
    private final KakaoFeignClient kakaoFeignClient;
    private final KakaoProperties kakaoProperties;

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
}
