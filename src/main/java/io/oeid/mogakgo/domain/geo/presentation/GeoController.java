package io.oeid.mogakgo.domain.geo.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.GeoSwagger;
import io.oeid.mogakgo.domain.geo.presentation.dto.res.UserRegionInfoAPIRes;
import io.oeid.mogakgo.domain.geo.application.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class GeoController implements GeoSwagger {

    private final GeoService geoService;

    @GetMapping("/areacode")
    public ResponseEntity<UserRegionInfoAPIRes> getUserRegionInfoByGPS(
        @UserId Long userId, @RequestParam Double longitude, @RequestParam Double latitude
    ) {
        int areaCode = geoService.getUserRegionInfoAboutCoordinates(userId, longitude, latitude);
        return ResponseEntity.ok(UserRegionInfoAPIRes.from(areaCode));
    }

}
