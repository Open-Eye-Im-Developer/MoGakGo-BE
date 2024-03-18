package io.oeid.mogakgo.domain.user.util;

import static reactor.core.publisher.Flux.merge;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserGithubUtil {

    private final String accessToken;

    public UserGithubUtil(@Value("${auth.github-access-token}") String accessToken) {
        this.accessToken = "Bearer " + accessToken;
    }

    public Map<String, Integer> updateUserDevelopLanguage(String repositoryUrl) {
        var repositoriesData = getRepositoriesData(repositoryUrl);
        if (repositoriesData == null) {
            return Map.of();
        }
        var monoList = repositoriesData.stream().map(
            map -> {
                var languageUrl = String.valueOf(map.get("languages_url"));
                return generateMonoByLanguageUrl(languageUrl);
            }
        ).toList();
        Map<String, Integer> lanugaeMap = new ConcurrentHashMap<>();
        merge(monoList).doOnEach(
            mono -> {
                var map = mono.get();
                if (map == null) {
                    return;
                }
                for (Entry<String, Integer> entry : map.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    lanugaeMap.put(key, lanugaeMap.getOrDefault(key, 0) + value);
                }
            }
        ).blockLast();
        return lanugaeMap;
    }

    private List<Map<String, Object>> getRepositoriesData(String repositoryUrl) {
        var webClient = WebClient.builder()
            .defaultHeader("Authorization", accessToken)
            .baseUrl(repositoryUrl)
            .build();
        return webClient.get().retrieve().bodyToMono(
            new ParameterizedTypeReference<List<Map<String, Object>>>() {
            }
        ).block();
    }

    private Mono<Map<String, Integer>> generateMonoByLanguageUrl(String languageUrl) {
        var webClient = WebClient.builder()
            .defaultHeader("Authorization", accessToken)
            .baseUrl(languageUrl)
            .build();
        return webClient.get().retrieve().bodyToMono(
            new ParameterizedTypeReference<>() {
            }
        );
    }
}
