package io.oeid.core_security.configuration;

import io.oeid.core_security.jwt.JwtAuthenticationFilter;
import io.oeid.core_security.jwt.JwtHelper;
import io.oeid.core_security.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtProperties properties;
    private final UserDetailsService userDetailsService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .rememberMe(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .headers(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(
                httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(
                    userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2UserService)
                );
                httpSecurityOAuth2LoginConfigurer.successHandler(successHandler);
            })
            .build();
    }

    @Bean
    protected JwtHelper jwtHelper() {
        return new JwtHelper(
            properties.getIssuer(),
            properties.getAccessTokenExpiryHour(),
            properties.getRefreshTokenExpiryHour(),
            properties.getClientSecret(),
            userDetailsService
        );
    }

    @Bean
    protected JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
            properties.getHeader(),
            jwtHelper()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
