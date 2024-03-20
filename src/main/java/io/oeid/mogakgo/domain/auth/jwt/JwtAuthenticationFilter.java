package io.oeid.mogakgo.domain.auth.jwt;

import static java.util.Collections.emptyList;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.oeid.mogakgo.core.properties.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String IDENTIFICATION_TYPE = "Bearer ";

    private final String header;
    private final JwtHelper jwtHelper;

    public JwtAuthenticationFilter(JwtProperties jwtProperties, JwtHelper jwtHelper) {
        this.header = jwtProperties.getHeader();
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String accessToken =
            request.getHeader(header) != null ? URLDecoder.decode(request.getHeader(header),
                StandardCharsets.UTF_8) : null;
        log.info("accessToken: {}", accessToken);
        if (accessToken == null || !accessToken.contains(IDENTIFICATION_TYPE)) {
            request.setAttribute("exception", new JWTVerificationException("Invalid token type"));
        } else {
            accessToken = accessToken.substring(7);
            try {
                var claims = jwtHelper.verify(accessToken);
                long userId = claims.get(JwtHelper.USER_ID_STR).asLong();
                List<GrantedAuthority> authorities = getAuthorities(
                    claims.get(JwtHelper.ROLES_STR).asArray(String.class));
                var authentication = new JwtAuthenticationToken(
                    JwtToken.of(userId, accessToken), "", authorities);
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                request.setAttribute(JwtHelper.USER_ID_STR, userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.warn(e.getMessage());
                request.setAttribute("exception", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> getAuthorities(String[] roles) {
        return roles == null || roles.length == 0 ? emptyList() : Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();
    }

}
