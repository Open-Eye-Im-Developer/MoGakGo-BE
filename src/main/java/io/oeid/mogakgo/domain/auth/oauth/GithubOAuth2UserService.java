package io.oeid.mogakgo.domain.auth.oauth;

import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    private final UserJpaRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
        long githubPk = Integer.toUnsignedLong(oAuth2User.getAttribute(userNameAttributeName));
        User user = manageUserEntity(githubPk, oAuth2User);
        return generateOAuth2User(userNameAttributeName, user.getId(), user);
    }

    private OAuth2User generateOAuth2User(String nameAttributeKey, long id, User user) {
        Map<String, Object> attributes = Map.of(
            nameAttributeKey, id,
            "user", user
        );
        return new DefaultOAuth2User(user.getAuthorities(), attributes, nameAttributeKey);
    }

    private User manageUserEntity(long githubPk, OAuth2User oAuth2User) {
        String githubId = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String githubUrl = oAuth2User.getAttribute("html_url");
        User user = userRepository.findByGithubPk(githubPk).orElseGet(
            () -> userRepository.save(User.of(githubPk, githubId, avatarUrl, githubUrl)));
        user.updateGithubInfo(githubId, avatarUrl, githubUrl);
        return user;
    }

}
