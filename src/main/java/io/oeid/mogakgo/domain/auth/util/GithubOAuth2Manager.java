package io.oeid.mogakgo.domain.auth.util;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.domain.auth.exception.AuthException;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubOAuth2Manager {

    private final String clientId;
    private final String clientSecret;

    public GithubOAuth2Manager(@Value("${github.oauth2.registration.client-id}") String clientId,
        @Value("${github.oauth2.registration.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken(String code) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://github.com/login/oauth/access_token")
            .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
            .build();
        var result = webClient.post().uri(uriBuilder ->
            uriBuilder
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .build()
        ).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
        }).block();
        return Objects.requireNonNull(result).get("access_token");
    }

    public Map<String, Object> getGithubUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com/user")
            .build();
        return webClient.get().header(AUTHORIZATION, "Bearer " + accessToken).retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
            }).doOnError(error -> {
                throw new AuthException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
            }).block();
    }
}
