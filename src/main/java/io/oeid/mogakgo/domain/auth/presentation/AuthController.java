package io.oeid.mogakgo.domain.auth.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.oeid.mogakgo.common.swagger.template.AuthSwagger;
import io.oeid.mogakgo.domain.auth.application.AuthService;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthTokenApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<AuthTokenApiResponse> reissue(
        @RequestHeader(AUTHORIZATION) String accessToken,
        @RequestBody @NonNull String refreshToken) {
        var accessTokenDto = authService.reissue(accessToken, refreshToken);
        return ResponseEntity.ok(
            AuthTokenApiResponse.of(accessTokenDto.getAccessToken(), null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenApiResponse> login(@RequestParam String code) {
        var response = authService.loginViaGithubCode(code);
        return ResponseEntity.ok(
            AuthTokenApiResponse.of(response.getAccessToken(), response.getRefreshToken(), response.getSignUpCompleteYn()));
    }

}
