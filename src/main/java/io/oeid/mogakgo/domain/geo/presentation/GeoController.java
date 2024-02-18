package io.oeid.mogakgo.domain.geo.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.GeoSwagger;
import io.oeid.mogakgo.domain.geo.presentation.dto.req.UserRegionInfoAPIReq;
import io.oeid.mogakgo.domain.geo.presentation.dto.res.UserRegionInfoAPIRes;
import io.oeid.mogakgo.domain.geo.application.GeoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class GeoController implements GeoSwagger {

    private final GeoService geoService;

    @PostMapping("/areacode")
    public ResponseEntity<UserRegionInfoAPIRes> getUserRegionInfoByGPS(
        @UserId Long userId, @Valid @RequestBody UserRegionInfoAPIReq request
    ) {
        int areaCode = geoService.getUserRegionInfoAboutCoordinates(
            userId, request.getUserId(), request.getLongitude(), request.getLatitude()
        );
        return ResponseEntity.ok(UserRegionInfoAPIRes.from(areaCode));
    }

}
