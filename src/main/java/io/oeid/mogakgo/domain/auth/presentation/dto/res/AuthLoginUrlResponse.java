package io.oeid.mogakgo.domain.auth.presentation.dto.res;

import lombok.Getter;

@Getter
public class AuthLoginUrlResponse {

    private final String loginUrl;

    private AuthLoginUrlResponse(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public static AuthLoginUrlResponse from(String loginUrl) {
        return new AuthLoginUrlResponse(loginUrl);
    }
}
