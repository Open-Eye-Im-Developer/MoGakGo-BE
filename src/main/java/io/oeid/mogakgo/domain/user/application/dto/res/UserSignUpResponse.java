package io.oeid.mogakgo.domain.user.application.dto.res;

import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpResponse {

    private Long userId;
    private String username;
    private String githubId;
    private String avatarUrl;
    private String githubUrl;
    private List<WantedJob> wantedJobs;

    public static UserSignUpResponse from(User user) {
        return new UserSignUpResponse(
            user.getId(),
            user.getUsername(),
            user.getGithubId(),
            user.getAvatarUrl(),
            user.getGithubUrl(),
            user.getUserWantedJobTags().stream().map(UserWantedJobTag::getWantedJob).toList()
        );
    }
}
