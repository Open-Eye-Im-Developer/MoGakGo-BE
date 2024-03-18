package io.oeid.mogakgo.domain.auth.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 재발급 요청")
public class AuthReissueApiRequest {

    @Schema(description = "Refresh Token")
    private String refreshToken;
}
