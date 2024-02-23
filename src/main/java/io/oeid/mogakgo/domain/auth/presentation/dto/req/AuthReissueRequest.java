package io.oeid.mogakgo.domain.auth.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 재발급 요청")
public class AuthReissueRequest {

    @Schema(description = "Refresh Token")
    private String refreshToken;
}
