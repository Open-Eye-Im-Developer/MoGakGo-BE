package io.oeid.mogakgo.domain.geo.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자가 현재 위치한 구역에 대한 법정구역코드 요청")
@Getter
public class UserRegionInfoAPIReq {

    @Schema(description = "법정구역코드를 요청한 사용자 ID", example = "2", implementation = Long.class)
    @NotNull
    private final Long userId;

    @Schema(description = "사용자의 GPS 기반 경도", example = "127.0783098757533", implementation = Double.class)
    @NotNull
    @DecimalMin("123.0")
    @DecimalMax("132.0")
    private final Double longitude;

    @Schema(description = "사용자의 GPS 기반 위도", example = "37.63338336616322", implementation = Double.class)
    @NotNull
    @DecimalMin("32.0")
    @DecimalMax("39.0")
    private final Double latitude;

    private UserRegionInfoAPIReq(Long userId, Double longitude, Double latitude) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static UserRegionInfoAPIReq of(Long userId, Double longitude, Double latitude) {
        return new UserRegionInfoAPIReq(userId, longitude, latitude);
    }
}
