package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserSignUpResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpApiResponse {

    private Long userId;
    private String username;
    private String githubId;
    private String avatarUrl;
    private String githubUrl;
    private List<String> developLanguages;
    private List<String> wantedJobs;

    public static UserSignUpApiResponse from(UserSignUpResponse response) {
        return new UserSignUpApiResponse(
            response.getUserId(),
            response.getUsername(),
            response.getGithubId(),
            response.getAvatarUrl(),
            response.getGithubUrl(),
            response.getDevelopLanguages().stream().map(Enum::name).toList(),
            response.getWantedJobs().stream().map(Enum::name).toList()
        );
    }
}
