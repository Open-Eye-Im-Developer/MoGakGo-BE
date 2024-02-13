package io.oeid.core_security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String IDENTIFICATION_TYPE = "Bearer ";

    private final String header;
    private final JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String accessToken =
            request.getHeader(header) != null ? URLDecoder.decode(request.getHeader(header),
                StandardCharsets.UTF_8) : null;
        if (accessToken != null) {
            if (!accessToken.contains(IDENTIFICATION_TYPE)) {
                request.setAttribute("exception",
                    new JWTVerificationException("Invalid token type"));
            } else {
                accessToken = accessToken.substring(7);
                try {
                    var authentication = jwtHelper.verify(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    request.setAttribute("exception", e);
                }
            }
        }
    }

}
