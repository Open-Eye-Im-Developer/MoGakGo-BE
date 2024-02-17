package io.oeid.mogakgo.domain.geo.feign;

import io.oeid.mogakgo.domain.geo.feign.dto.AddressInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "kakaoFeignClient",
    url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json"
)
public interface KakaoFeignClient {

    @GetMapping
    AddressInfoDto getAreaCodeAboutCoordinates(
        @RequestHeader("Authorization") String token,
        @RequestParam("x") Double x, @RequestParam("y") Double y
        // x: longitude (위도), y: latitude (경도)
    );
}
