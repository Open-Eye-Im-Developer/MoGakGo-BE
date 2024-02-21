package io.oeid.mogakgo.domain.auth.oauth;

import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationToken;
import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import io.oeid.mogakgo.domain.auth.jwt.JwtRedisDao;
import io.oeid.mogakgo.domain.auth.jwt.JwtToken;
import io.oeid.mogakgo.domain.user.application.UserOAuth2Service;
import io.oeid.mogakgo.domain.user.application.dto.res.UserOAuth2Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtHelper jwtHelper;
    private final JwtRedisDao jwtRedisDao;
    private final UserOAuth2Service userOAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success -> onAuthenticationSuccess");
        // 인증 완료된 유저 정보를 불러오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 인증 완료된 유저 정보 기반 유저 엔티티 관리 및 결과 반환
        var userOAuth2Response = manageUserEntity(Long.parseLong(oAuth2User.getName()), oAuth2User);
        // JWT 토큰 생성 및 저장
        var jwtToken = generateJwtToken(userOAuth2Response);
        // 완료된 인증 정보 기반 새로운 authentication 생성
        oAuth2User = generateAuthentication(userOAuth2Response, jwtToken);
        // 새로운 authentication을 SecurityContextHolder에 저장
        authentication = new JwtAuthenticationToken(oAuth2User, null,
            oAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 로그인 성공 페이지로 리다이렉트
        response.sendRedirect("/oauth2/login/success");
    }

    private UserOAuth2Response manageUserEntity(long githubPk, OAuth2User oAuth2User) {
        String githubId = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String githubUrl = oAuth2User.getAttribute("html_url");
        String repositoryUrl = oAuth2User.getAttribute("repos_url");
        return userOAuth2Service.manageOAuth2User(githubPk, githubId, avatarUrl, githubUrl,
            repositoryUrl);
    }

    private JwtToken generateJwtToken(UserOAuth2Response response) {
        long userId = response.getUserId();
        String[] roles = response.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .toArray(String[]::new);
        var jwtToken = jwtHelper.sign(userId, roles);
        jwtRedisDao.saveTokens(jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    private OAuth2User generateAuthentication(UserOAuth2Response oAuth2Response,
        JwtToken jwtToken) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", oAuth2Response.getUserId());
        attributes.put("signUpComplete", oAuth2Response.getSignUpComplete());
        attributes.put("accessToken", jwtToken.getAccessToken());
        attributes.put("refreshToken", jwtToken.getRefreshToken());
        attributes.put("refreshTokenExpireTime", jwtToken.getRefreshTokenExpirySeconds());
        return new DefaultOAuth2User(oAuth2Response.getAuthorities(), attributes, "id");
    }
}
