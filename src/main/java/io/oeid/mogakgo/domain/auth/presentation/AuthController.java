package io.oeid.mogakgo.domain.auth.presentation;

import io.oeid.mogakgo.common.swagger.template.AuthSwagger;
import io.oeid.mogakgo.domain.auth.application.AuthService;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<AuthAccessTokenResponse> reissue(
        @RequestHeader("Authorization") String accessToken,
        @CookieValue("refreshToken") String refreshToken) {
        accessToken = accessToken.substring(7);
        var accessTokenDto = authService.reissue(accessToken, refreshToken);
        return ResponseEntity.ok(AuthAccessTokenResponse.of(accessTokenDto.getAccessToken(), null));
    }
}
