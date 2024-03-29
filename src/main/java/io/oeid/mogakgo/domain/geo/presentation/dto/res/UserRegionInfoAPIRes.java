package io.oeid.mogakgo.domain.geo.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "사용자 GPS 정보에 대한 법정구역코드 응답. 사용자가 현재 위치한 법정구역코드를 반환한다.")
@Getter
public class UserRegionInfoAPIRes {

    @Schema(description = "사용자의 법정구역코드", example = "11110", implementation = Integer.class)
    private final int areaCode;

    private UserRegionInfoAPIRes(int areaCode) {
        this.areaCode = areaCode;
    }

    public static UserRegionInfoAPIRes from(int areaCode) {
        return new UserRegionInfoAPIRes(areaCode);
    }
}
