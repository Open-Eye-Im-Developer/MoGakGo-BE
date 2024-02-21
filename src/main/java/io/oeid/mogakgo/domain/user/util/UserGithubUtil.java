package io.oeid.mogakgo.domain.user.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserGithubUtil {

    public Map<String, Integer> updateUserDevelopLanguage(String repositoryUrl) {
        WebClient webClient = WebClient.create(repositoryUrl);
        Map<String, Integer> languageMap = new HashMap<>();
        List<Object> response = webClient.get().retrieve().bodyToMono(List.class).block();
        if (response == null) {
            return Map.of();
        }
        response.forEach(o -> {
            Map<String, Object> json = (Map<String, Object>) o;
            var languageWebClient = WebClient.create((String) json.get("languages_url"));
            Map<String, Integer> languages = languageWebClient.get().retrieve()
                .bodyToMono(Map.class).block();
            if (languages == null) {
                return;
            }
            languages.forEach(
                (lang, size) -> languageMap.put(lang, languageMap.getOrDefault(lang, 0) + size));
        });
        List<String> languageKeys = new ArrayList<>(languageMap.keySet());
        languageKeys.sort((o1, o2) -> languageMap.get(o2).compareTo(languageMap.get(o1)));
        Map<String, Integer> result = new LinkedHashMap<>();
        languageKeys.subList(0, 3).forEach(key -> result.put(key, languageMap.get(key)));
        return result;
    }
}
