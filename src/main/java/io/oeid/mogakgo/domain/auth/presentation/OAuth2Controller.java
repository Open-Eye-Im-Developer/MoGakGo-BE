package io.oeid.mogakgo.domain.auth.presentation;

import io.oeid.mogakgo.common.swagger.template.OAuth2Swagger;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthLoginUrlResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@RequestMapping("/oauth2")
public class OAuth2Controller implements OAuth2Swagger {

    private final String serverUrl;
    private final String loginRedirectUrl;

    public OAuth2Controller(@Value("${auth.server-url}") String serverUrl,
        @Value("${auth.login-redirect-url}") String loginRedirectUrl) {
        this.serverUrl = serverUrl;
        this.loginRedirectUrl = loginRedirectUrl;
    }

    @GetMapping("/login/success")
    public void loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User,
        HttpServletResponse response) throws IOException {
        String accessToken = oAuth2User.getAttributes().get("accessToken").toString();
        boolean signUpComplete = (boolean) oAuth2User.getAttributes().get("signUpComplete");
        String refreshToken = oAuth2User.getAttributes().get("refreshToken").toString();
        int refreshTokenExpireTime = (int) oAuth2User.getAttributes().get("refreshTokenExpireTime");
        setCookie(refreshToken, refreshTokenExpireTime, response);
        response.sendRedirect(
            loginRedirectUrl + "?accessToken=" + accessToken + "&signUpComplete=" + signUpComplete);
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
