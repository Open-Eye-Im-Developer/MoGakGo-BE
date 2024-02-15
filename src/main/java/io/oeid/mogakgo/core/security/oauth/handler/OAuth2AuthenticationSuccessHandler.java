package io.oeid.mogakgo.core.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.core.security.jwt.JwtHelper;
import io.oeid.mogakgo.core.security.jwt.JwtToken;
import io.oeid.mogakgo.domain.user.domain.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final JwtHelper jwtHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        JwtToken jwtToken = jwtHelper.sign(Long.parseLong(oAuth2User.getName()),
            new String[]{Role.ROLE_USER.name()});
        updateServletResponse(response, jwtToken);
    }

    // TODO: Save Access Token and Refresh Token in Redis

    private void updateServletResponse(HttpServletResponse response, JwtToken jwtToken)
        throws IOException {
        String body = OBJECT_MAPPER.writeValueAsString(jwtToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }
}
