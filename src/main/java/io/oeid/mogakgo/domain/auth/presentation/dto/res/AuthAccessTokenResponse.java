package io.oeid.mogakgo.domain.auth.presentation.dto.res;

import lombok.Getter;

@Getter
public class AuthAccessTokenResponse {

    private final String accessToken;

    private AuthAccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static AuthAccessTokenResponse from(String accessToken) {
        return new AuthAccessTokenResponse(accessToken);
    }
}
