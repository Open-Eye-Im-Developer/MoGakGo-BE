package io.oeid.mogakgo.domain.project.domain.entity.vo;

import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatorInfo {

    @Column(name = "user_github_id")
    private String userGithubId;

    @Column(name = "bio", length = 50)
    private String bio;

    @Column(name = "jandi_rating", nullable = false)
    private Double jandiRating;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "main_achievement_id", nullable = false)
    private Long mainAchievementId;

    private CreatorInfo(User creator, Long mainAchievementId) {
        if (creator == null) {
            throw new RuntimeException("creator is required!");
        }
        this.userGithubId = creator.getGithubId();
        this.bio = creator.getBio();
        this.jandiRating = creator.getJandiRate();
        this.username = creator.getUsername();
        this.avatarUrl = creator.getAvatarUrl();
        this.region = setRegionWithValidation(creator);
        this.mainAchievementId = mainAchievementId;
    }

    public static CreatorInfo of(User creator, Long mainAchievementId) {
        return new CreatorInfo(creator, mainAchievementId);
    }

    private String setRegionWithValidation(User user) {
        if (user.getRegion() == null) {
            throw new RuntimeException("need to authenticate your neighborhood!");
        }
        return user.getRegion();
    }
}
