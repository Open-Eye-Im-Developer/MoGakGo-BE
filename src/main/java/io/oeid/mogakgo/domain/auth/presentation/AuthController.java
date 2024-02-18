package io.oeid.mogakgo.domain.auth.presentation;

import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthAccessTokenResponse;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthLoginUrlResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthController {

    private final String serverUrl;

    public AuthController(@Value("${auth.server-url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @GetMapping("/login/success")
    public ResponseEntity<AuthAccessTokenResponse> loginSuccess(
        @AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) {
        String accessToken = oAuth2User.getAttributes().get("accessToken").toString();
        String refreshToken = oAuth2User.getAttributes().get("refreshToken").toString();
        int refreshTokenExpireTime = (int) oAuth2User.getAttributes().get("refreshTokenExpireTime");
        boolean signUpComplete = (boolean) oAuth2User.getAttributes().get("signUpComplete");
        setCookie(refreshToken, refreshTokenExpireTime, response);
        return ResponseEntity.ok(AuthAccessTokenResponse.of(accessToken, signUpComplete));
    }

    @GetMapping("/login")
    public ResponseEntity<AuthLoginUrlResponse> login() {
        return ResponseEntity.ok(
            AuthLoginUrlResponse.from(serverUrl + "/oauth2/authorization/github"));
    }

    private void setCookie(String refreshToken, int refreshTokenExpireTime,
        HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(refreshTokenExpireTime);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
