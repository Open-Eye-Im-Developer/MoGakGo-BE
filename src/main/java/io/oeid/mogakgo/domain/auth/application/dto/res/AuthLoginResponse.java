package io.oeid.mogakgo.domain.auth.application.dto.res;

import io.oeid.mogakgo.domain.auth.jwt.JwtToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthLoginResponse {

    private String accessToken;
    private String refreshToken;
    private int refreshTokenExpirySeconds;
    private Boolean signUpCompleteYn;

    public static AuthLoginResponse of(AuthOAuth2Response authOAuth2Response, JwtToken jwtToken) {
        return new AuthLoginResponse(jwtToken.getAccessToken(), jwtToken.getRefreshToken(),
            jwtToken.getRefreshTokenExpirySeconds(), authOAuth2Response.getSignUpComplete());
    }
}
