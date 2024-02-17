package io.oeid.mogakgo.domain.auth.oauth;

import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationToken;
import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import io.oeid.mogakgo.domain.auth.jwt.JwtRedisDao;
import io.oeid.mogakgo.domain.auth.jwt.JwtToken;
import io.oeid.mogakgo.domain.user.domain.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtHelper jwtHelper;
    private final JwtRedisDao jwtRedisDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        JwtToken jwtToken = jwtHelper.sign(Long.parseLong(oAuth2User.getName()),
            new String[]{Role.ROLE_USER.name()});
        jwtRedisDao.saveTokens(jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", Long.parseLong(oAuth2User.getName()));
        attributes.put("accessToken", jwtToken.getAccessToken());
        attributes.put("refreshToken", jwtToken.getRefreshToken());
        oAuth2User = new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "id");
        authentication = new JwtAuthenticationToken(oAuth2User, null,
            oAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
