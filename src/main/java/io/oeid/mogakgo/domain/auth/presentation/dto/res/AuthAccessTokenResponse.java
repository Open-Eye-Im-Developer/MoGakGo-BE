package io.oeid.mogakgo.domain.auth.presentation.dto.res;

import lombok.Getter;

@Getter
public class AuthAccessTokenResponse {

    private final String accessToken;
    private final Boolean signUpComplete;

    private AuthAccessTokenResponse(String accessToken, Boolean signUpComplete) {
        this.accessToken = accessToken;
        this.signUpComplete = signUpComplete;
    }

    public static AuthAccessTokenResponse of(String accessToken, boolean signUpComplete) {
        return new AuthAccessTokenResponse(accessToken, signUpComplete);
    }
}
