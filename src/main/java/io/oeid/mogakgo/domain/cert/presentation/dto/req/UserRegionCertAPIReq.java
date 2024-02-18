package io.oeid.mogakgo.domain.cert.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자가 해당 코드에 해당하는 서비스 지역의 동네 인증을 요청")
@Getter
public class UserRegionCertAPIReq {

    @Schema(description = "동네 인증을 요청한 사용자 ID", example = "2", implementation = Long.class)
    @NotNull
    private final Long userId;

    @Schema(description = "동네 인증을 요청하는 서비스 지역의 법정구역코드")
    @NotNull
    private final Integer areaCode;

    private UserRegionCertAPIReq(Long userId, Integer areaCode) {
        this.userId = userId;
        this.areaCode = areaCode;
    }

    public static UserRegionCertAPIReq of(Long userId, Integer areaCode) {
        return new UserRegionCertAPIReq(userId, areaCode);
    }
}
