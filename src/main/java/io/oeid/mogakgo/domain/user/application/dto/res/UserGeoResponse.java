package io.oeid.mogakgo.domain.user.application.dto.res;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGeoResponse {
    private Region region;
    private LocalDateTime regionAuthenticationAt;
}
