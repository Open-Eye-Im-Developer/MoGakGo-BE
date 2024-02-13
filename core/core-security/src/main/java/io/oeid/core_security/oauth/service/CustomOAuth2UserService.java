package io.oeid.core_security.oauth.service;

import io.oeid.domain_user.domain.User;
import io.oeid.domain_user.infrastructure.UserJpaRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserJpaRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
        long id = Integer.toUnsignedLong(oAuth2User.getAttribute(userNameAttributeName));
        User user = mangeUserEntity(id, oAuth2User);
        return generateOAuth2User(userNameAttributeName, id, user);
    }

    private OAuth2User generateOAuth2User(String nameAttributeKey, long id, User user) {
        Map<String, Object> attributes = Map.of(
            nameAttributeKey, id,
            "user", user
        );
        return new DefaultOAuth2User(user.getAuthorities(), attributes, nameAttributeKey);
    }

    private User mangeUserEntity(long userId, OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String githubUrl = oAuth2User.getAttribute("html_url");
        return userRepository.findById(userId).orElseGet(
            () -> userRepository.save(User.of(userId, username, avatarUrl, githubUrl)));
    }
}
