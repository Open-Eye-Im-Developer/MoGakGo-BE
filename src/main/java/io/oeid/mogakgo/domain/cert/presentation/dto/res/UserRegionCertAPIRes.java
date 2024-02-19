package io.oeid.mogakgo.domain.cert.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "동네 인증 완료 응답. 인증을 수행한 사용자의 ID를 반환한다.")
@Getter
public class UserRegionCertAPIRes {

    @Schema(description = "동네 인증을 수행한 사용자 ID", example = "2", implementation = Long.class)
    private final Long userId;

    private UserRegionCertAPIRes(Long userId) {
        this.userId = userId;
    }

    public static UserRegionCertAPIRes from(Long userId) {
        return new UserRegionCertAPIRes(userId);
    }

}
