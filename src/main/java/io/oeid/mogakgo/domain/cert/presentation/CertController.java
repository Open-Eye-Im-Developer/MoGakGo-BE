package io.oeid.mogakgo.domain.cert.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.CertSwagger;
import io.oeid.mogakgo.domain.cert.application.CertService;
import io.oeid.mogakgo.domain.cert.presentation.dto.req.UserRegionCertAPIReq;
import io.oeid.mogakgo.domain.cert.presentation.dto.req.UserRegionInfoAPIReq;
import io.oeid.mogakgo.domain.cert.presentation.dto.res.UserRegionCertAPIRes;
import io.oeid.mogakgo.domain.cert.presentation.dto.res.UserRegionInfoAPIRes;
import io.oeid.mogakgo.domain.geo.application.GeoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/cert")
@RestController
@RequiredArgsConstructor
public class CertController implements CertSwagger {

    private final CertService certService;
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

    @PostMapping("/certificate")
    public ResponseEntity<UserRegionCertAPIRes> certificateNeighborhood(
        @UserId Long userId, @Valid @RequestBody UserRegionCertAPIReq request
    ) {
        Long id = certService.certificate(userId, request.getUserId(), request.getAreaCode());
        return ResponseEntity.ok(UserRegionCertAPIRes.from(id));
    }
}
