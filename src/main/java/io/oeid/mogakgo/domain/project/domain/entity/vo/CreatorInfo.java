package io.oeid.mogakgo.domain.project.domain.entity.vo;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_NULL_DATA;
import static io.oeid.mogakgo.exception.code.ErrorCode400.NOT_MATCH_MEET_LOCATION;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private Region region;

    @Column(name = "main_achievement_id", nullable = false)
    private Long mainAchievementId;

    private CreatorInfo(User creator, Long mainAchievementId) {
        if (creator == null) {
            throw new ProjectException(INVALID_PROJECT_NULL_DATA);
        }
        this.userGithubId = creator.getGithubId();
        this.bio = creator.getBio();
        this.jandiRating = creator.getJandiRate();
        this.username = creator.getUsername();
        this.avatarUrl = creator.getAvatarUrl();
        //TODO: 유효성 검사 필요한지 고민
        this.region = creator.getRegion();
        this.mainAchievementId = mainAchievementId;
        validateRegion();
    }

    public static CreatorInfo of(User creator, Long mainAchievementId) {
        return new CreatorInfo(creator, mainAchievementId);
    }

    private void validateRegion() {
        if (region == null) {
            throw new ProjectException(NOT_MATCH_MEET_LOCATION);
        }
    }
}
