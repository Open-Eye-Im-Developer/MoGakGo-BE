package io.oeid.mogakgo.domain.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import io.oeid.mogakgo.core.properties.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("필터 테스트: JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private JwtHelper jwtHelper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 토큰_인증_성공() throws Exception {
        // Arrange
        var expectedUserId = 1L;
        var expectedRoles = new String[]{"ROLE_USER"};

        Map<String, Claim> claims = Map.of(
            JwtHelper.USER_ID_STR, mock(Claim.class),
            JwtHelper.ROLES_STR, mock(Claim.class)
        );
        when(request.getHeader(any())).thenReturn("Bearer validToken");
        when(jwtHelper.verify(anyString())).thenReturn(claims);
        when(claims.get(JwtHelper.USER_ID_STR).asLong()).thenReturn(expectedUserId);
        when(claims.get(JwtHelper.ROLES_STR).asArray(String.class)).thenReturn(expectedRoles);
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // Assert
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(
            expectedUserId);
    }

    @Test
    void 토큰_인증_실패_잘못된_토큰() throws Exception {
        // Arrange
        when(request.getHeader(any())).thenReturn("Bearer invalidToken");
        when(jwtHelper.verify(anyString())).thenThrow(JWTVerificationException.class);
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // Assert
        verify(filterChain).doFilter(request, response);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void 토큰_인증_실패_해더에_존재하지_않을_때() throws Exception {
        // Arrange
        when(request.getHeader(any())).thenReturn(null);
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // Assert
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}