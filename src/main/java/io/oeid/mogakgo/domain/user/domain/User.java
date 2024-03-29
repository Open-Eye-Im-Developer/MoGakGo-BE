package io.oeid.mogakgo.domain.user.domain;

import static io.oeid.mogakgo.exception.code.ErrorCode400.ACHIEVEMENT_SHOULD_BE_DIFFERENT;
import static io.oeid.mogakgo.exception.code.ErrorCode400.USER_AVAILABLE_LIKE_COUNT_IS_ZERO;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import io.oeid.mogakgo.domain.user.domain.enums.Role;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Entity
@Table(name = "user_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private static final int MAX_TAG_SIZE = 3;
    private static final double JANDI_WEIGHT = 2.5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "github_pk")
    private Long githubPk;

    @Column(name = "username")
    private String username;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("byteSize DESC")
    private final List<UserDevelopLanguageTag> userDevelopLanguageTags = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserWantedJobTag> userWantedJobTags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "bio", length = 50)
    private String bio;

    @Column(name = "jandi_rate")
    private double jandiRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @Column(name = "region_authentication_at")
    private LocalDateTime regionAuthenticationAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "available_join_count")
    private int availableJoinCount;

    @Column(name = "available_like_count")
    private int availableLikeCount;

    @Column(name = "signup_yn")
    private Boolean signupYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    private User(Long githubPk, String githubId, String avatarUrl, String githubUrl,
        String repositoryUrl) {
        this.githubPk = githubPk;
        this.username = githubId;
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.repositoryUrl = repositoryUrl;
        this.role = Role.ROLE_USER;
        this.jandiRate = 10d;
        this.signupYn = false;
    }

    public static User of(long githubPk, String username, String avatarUrl, String githubUrl,
        String repositoryUrl) {
        return new User(githubPk, username, avatarUrl, githubUrl, repositoryUrl);
    }

    protected void addDevelopLanguage(UserDevelopLanguageTag userDevelopLanguageTag) {
        if (userDevelopLanguageTags.size() + 1 > MAX_TAG_SIZE) {
            throw new UserException(ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
        }
        userDevelopLanguageTags.add(userDevelopLanguageTag);
    }

    protected void addWantedJob(UserWantedJobTag userWantedJobTag) {
        if (userWantedJobTags.size() + 1 > MAX_TAG_SIZE) {
            throw new UserException(ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
        }
        userWantedJobTags.add(userWantedJobTag);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void updateGithubInfo(String githubId, String avatarUrl, String githubUrl,
        String repositoryUrl) {
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.repositoryUrl = repositoryUrl;
    }

    public void updateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new UserException(ErrorCode400.USERNAME_SHOULD_BE_NOT_EMPTY);
        }
        this.username = username;
    }

    public void increaseAvailableLikeCount() {
        this.availableLikeCount += 1;
    }

    public void decreaseAvailableLikeCount() {
        if (this.availableLikeCount <= 0) {
            throw new UserException(USER_AVAILABLE_LIKE_COUNT_IS_ZERO);
        }
        this.availableLikeCount -= 1;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void deleteAllWantJobTags() {
        this.userWantedJobTags.clear();
    }

    public void deleteAllDevelopLanguageTags() {
        this.userDevelopLanguageTags.clear();
    }

    public void signUpComplete() {
        if (Boolean.TRUE.equals(signupYn)) {
            throw new UserException(ErrorCode400.USER_ALREADY_SIGNUP);
        }
        this.signupYn = true;
    }

    public void updateRegion(Region region) {
        if (region == null) {
            throw new UserException(ErrorCode400.USER_REGION_SHOULD_BE_NOT_EMPTY);
        }
        // 사용자가 아직 동네 인증을 하지 않았거나, 새롭게 인증하려는 지역이 이미 인증된 지역과 다를 경우만 동네 인증 처리
        if (validateAvailableRegionUpdate(region)) {
            this.region = region;
            this.regionAuthenticationAt = LocalDateTime.now();
        }
    }

    public void updateUserInfos(String username, String avatarUrl, String bio) {
        updateUsername(username);
        this.avatarUrl = verifyAvatarUrl(avatarUrl);
        this.bio = bio;
        deleteAllWantJobTags();
    }

    public void updateAchievement(Achievement achievement) {
        if (this.achievement != null && this.achievement.equals(achievement)) {
            throw new UserAchievementException(ACHIEVEMENT_SHOULD_BE_DIFFERENT);
        }
        this.achievement = achievement;
    }

    public void updateJandiRateByReview(ReviewRating rating, double time) {
        this.jandiRate += rating.getJandiValue() * time * JANDI_WEIGHT;
    }

    public void updateJandiRateByCancel() {
        this.jandiRate -= ReviewRating.ONE.getValue() * JANDI_WEIGHT;
    }

    private boolean validateAvailableRegionUpdate(Region region) {
        return this.region == null || !this.region.equals(region);
    }

    private String verifyAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new UserException(ErrorCode400.USER_AVATAR_URL_NOT_NULL);
        }
        return avatarUrl;
    }

}
