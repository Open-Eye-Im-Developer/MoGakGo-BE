package io.oeid.mogakgo.core.configuration;

import io.oeid.mogakgo.domain.auth.jwt.JwtAccessDeniedHandler;
import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationEntryPoint;
import io.oeid.mogakgo.domain.auth.jwt.JwtAuthenticationFilter;
import io.oeid.mogakgo.domain.auth.oauth.OAuth2AuthenticationSuccessHandler;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(OAuth2AuthenticationSuccessHandler authenticationSuccessHandler,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtAccessDeniedHandler jwtAccessDeniedHandler,
        JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain filterChainApi(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        return http
            .securityMatchers(matchers -> matchers.requestMatchers("/api/**"))
            .sessionManagement(
                management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                requests -> requests.anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(handling ->
                handling.accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChainOAuth2(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        return http
            .oauth2Login(oauth2 -> oauth2.successHandler(authenticationSuccessHandler))
            .authorizeHttpRequests(
                requests -> requests.anyRequest().permitAll())
            .build();
    }

    private void configureCommonSecuritySettings(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .rememberMe(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);
//            .headers(AbstractHttpConfigurer::disable);
    }

}
