package io.oeid.mogakgo.domain.auth.application.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthReissueResponse {

    private final String accessToken;

    public static AuthReissueResponse from(String accessToken) {
        return new AuthReissueResponse(accessToken);
    }
}
