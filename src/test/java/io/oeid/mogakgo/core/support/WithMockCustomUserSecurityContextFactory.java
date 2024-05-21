package io.oeid.mogakgo.core.support;

import static io.oeid.mogakgo.domain.user.domain.enums.Role.ROLE_USER;

import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationToken;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser mockCustomUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE_USER.name()));
        var auth = new JwtAuthenticationToken(mockCustomUser.userId(), null, authorities);
        context.setAuthentication(auth);
        return context;
    }
}
