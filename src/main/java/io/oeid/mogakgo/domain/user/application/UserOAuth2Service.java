package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.user.application.dto.res.UserOAuth2Response;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserOAuth2Service {

    private final UserJpaRepository userRepository;

    @Transactional
    public UserOAuth2Response manageOAuth2User(long githubPk, String githubId, String avatarUrl, String githubUrl,
        String repositoryUrl) {
        User user = userRepository.findByGithubPk(githubPk).orElseGet(() -> userRepository.save(
            User.of(githubPk, githubId, avatarUrl, githubUrl, repositoryUrl)));
        user.updateGithubInfo(githubId, avatarUrl, githubUrl, repositoryUrl);
        return UserOAuth2Response.from(user);
    }
}
