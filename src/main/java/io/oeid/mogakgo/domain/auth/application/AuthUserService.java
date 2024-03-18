package io.oeid.mogakgo.domain.auth.application;

import io.oeid.mogakgo.domain.auth.application.dto.res.AuthOAuth2Response;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final UserJpaRepository userRepository;

    @Transactional
    public AuthOAuth2Response manageOAuth2User(Map<String, Object> githubUserInfo) {
        var githubPk = Long.parseLong(githubUserInfo.get("id").toString());
        var githubId = githubUserInfo.get("login").toString();
        var avatarUrl = githubUserInfo.get("avatar_url").toString();
        var githubUrl = githubUserInfo.get("html_url").toString();
        var repositoryUrl = githubUserInfo.get("repos_url").toString();
        var user = userRepository.findByGithubPk(githubPk).orElseGet(() ->
            userRepository.save(User.of(githubPk, githubId, avatarUrl, githubUrl, repositoryUrl)));
        user.updateGithubInfo(githubId, avatarUrl, githubUrl, repositoryUrl);
        return AuthOAuth2Response.from(user);
    }

}
