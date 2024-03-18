package io.oeid.mogakgo.domain.auth.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import io.oeid.mogakgo.common.swagger.template.AuthSwagger;
import io.oeid.mogakgo.domain.auth.application.AuthService;
import io.oeid.mogakgo.domain.auth.presentation.dto.req.AuthReissueApiRequest;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthAccessTokenApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthAccessTokenApiResponse> reissue(
        @RequestHeader(AUTHORIZATION) String accessToken,
        @RequestBody AuthReissueApiRequest request) {
        var accessTokenDto = authService.reissue(accessToken, request.getRefreshToken());
        return ResponseEntity.ok(
            AuthAccessTokenApiResponse.of(accessTokenDto.getAccessToken(), null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthAccessTokenApiResponse> login(@RequestParam String code) {
        var response = authService.loginViaGithubCode(code);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",
                response.getRefreshToken())
            .httpOnly(true)
            .maxAge(response.getRefreshTokenExpirySeconds())
            .path("/")
            .build();
        return ResponseEntity.ok()
            .header(SET_COOKIE, responseCookie.toString())
            .body(AuthAccessTokenApiResponse.of(response.getAccessToken(),
                response.getSignUpCompleteYn()));
    }
}
