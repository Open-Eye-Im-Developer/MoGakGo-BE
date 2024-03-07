package io.oeid.mogakgo.domain.user.application.dto.req;

import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserUpdateApiReq;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUpdateReq {

    private final String username;
    private final String bio;
    private final String avatarUrl;
    private List<WantedJob> wantedJobs;

    public static UserUpdateReq from(UserUpdateApiReq request) {
        return new UserUpdateReq(
            request.getUsername(),
            request.getBio(),
            request.getAvatarUrl(),
            request.getWantedJobs().stream().map(WantedJob::valueOf).toList()
        );
    }
}
