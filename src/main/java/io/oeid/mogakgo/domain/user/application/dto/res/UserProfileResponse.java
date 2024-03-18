package io.oeid.mogakgo.domain.user.application.dto.res;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileResponse {

    private final Long id;
    private final String username;
    private final String githubId;
    private final String avatarUrl;
    private final String githubUrl;
    private final String bio;
    private final Double jandiRate;
    private final Long achievementId;
    private final String region;
    private final List<DevelopLanguage> developLanguages;
    private final List<WantedJob> wantedJobs;

    public static UserProfileResponse from(User user) {
        Achievement achievement = user.getAchievement();
        Region region = user.getRegion();
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getGithubId(),
            user.getAvatarUrl(),
            user.getGithubUrl(),
            user.getBio(),
            user.getJandiRate(),
            achievement == null ? null : achievement.getId(),
            region == null ? null : region.getDepth1() + " " + region.getDepth2(),
            user.getUserDevelopLanguageTags().stream().map(
                UserDevelopLanguageTag::getDevelopLanguage).toList(),
            user.getUserWantedJobTags().stream().map(
                UserWantedJobTag::getWantedJob).toList()
        );
    }
}
