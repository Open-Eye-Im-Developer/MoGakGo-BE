package io.oeid.mogakgo.domain.auth.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Access Token 재발급 응답")
@Getter
public class AuthTokenApiResponse {

    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJNb0dha0dvIiwiaWF0IjoxNzA4MjE5NDA3LCJleHAiOjE3NDQyMTk0MDcsInVzZXJJZCI6Miwicm9sZXMiOlsiUk9MRV9VU0VSIl19.vu_Oq5dX3cMYAOwFIk_BvqkEGrkk0Reth2FBde7pcKw")
    private final String accessToken;
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJNb0dha0dvIiwiaWF0IjoxNzA4MjE5NDA3LCJleHAiOjE3NDQyMTk0MDcsInVzZXJJZCI6Miwicm9sZXMiOlsiUk9MRV9VU0VSIl19.vu_Oq5dX3cMYAOwFIk_BvqkEGrkk0Reth2FBde7pcKw")
    private final String refreshToken;
    @Schema(description = "회원가입 완료 여부", example = "true", nullable = true)
    private final Boolean signUpComplete;

    private AuthTokenApiResponse(String accessToken, String refreshToken,
        Boolean signUpComplete) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.signUpComplete = signUpComplete;
    }

    public static AuthTokenApiResponse of(String accessToken, String refreshToken,
        Boolean signUpComplete) {
        return new AuthTokenApiResponse(accessToken, refreshToken, signUpComplete);
    }
}
