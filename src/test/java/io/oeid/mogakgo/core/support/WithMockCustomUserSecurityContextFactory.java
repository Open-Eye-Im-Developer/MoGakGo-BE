package io.oeid.mogakgo.core.support;

import io.oeid.mogakgo.core.properties.JwtProperties;
import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationToken;
import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

    private static JwtProperties initProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setHeader("test");
        properties.setIssuer("test");
        properties.setClientSecret("test");
        properties.setAccessTokenExpiryHour(1);
        properties.setRefreshTokenExpiryHour(3);
        return properties;
    }

    private final JwtHelper jwtHelper;

    public WithMockCustomUserSecurityContextFactory() {
        this.jwtHelper = new JwtHelper(
            initProperties()
        );
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser mockCustomUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var token = jwtHelper.sign(mockCustomUser.userId(),
            authorities.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
        var auth = new JwtAuthenticationToken(token, null, authorities);
        context.setAuthentication(auth);
        return context;
    }
}
