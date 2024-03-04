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
public class UserUpdateRes {
    private String username;
    private String bio;
    private String avatarUrl;
    private List<WantedJob> wantedJobs;

    public static UserUpdateRes from(User user){
        return new UserUpdateRes(
            user.getUsername(),
            user.getBio(),
            user.getAvatarUrl(),
            user.getUserWantedJobTags().stream().map(UserWantedJobTag::getWantedJob).toList()
        );
    }
}
