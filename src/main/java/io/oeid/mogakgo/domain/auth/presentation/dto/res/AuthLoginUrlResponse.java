package io.oeid.mogakgo.domain.auth.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 URL 반환 응답")
@Getter
public class AuthLoginUrlResponse {

    @Schema(description = "로그인 URL", example = "http://3.38.76.76:8080/oauth2/authorization/github")
    private final String loginUrl;

    private AuthLoginUrlResponse(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public static AuthLoginUrlResponse from(String loginUrl) {
        return new AuthLoginUrlResponse(loginUrl);
    }
}
