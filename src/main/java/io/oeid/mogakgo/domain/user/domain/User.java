package io.oeid.mogakgo.domain.user.domain;

import io.oeid.mogakgo.domain.achievement.domain.Achievement;
import io.oeid.mogakgo.domain.geo.domain.enums.Region;
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



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("byteSize ASC")
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

    private User(Long githubPk, String githubId, String avatarUrl, String githubUrl) {
        this.githubPk = githubPk;
        this.username = githubId;
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.role = Role.ROLE_USER;
        this.jandiRate = 0d;
        this.signupYn = false;
    }

    public static User of(long githubPk, String username, String avatarUrl, String githubUrl) {
        return new User(githubPk, username, avatarUrl, githubUrl);
    }

    public void addDevelopLanguage(UserDevelopLanguageTag userDevelopLanguageTag) {
        if (userDevelopLanguageTags.size() + 1 > MAX_TAG_SIZE) {
            throw new UserException(ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
        }
        userDevelopLanguageTags.add(userDevelopLanguageTag);
    }

    public void addWantedJob(UserWantedJobTag userWantedJobTag) {
        if (userWantedJobTags.size() + 1 > MAX_TAG_SIZE) {
            throw new UserException(ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
        }
        userWantedJobTags.add(userWantedJobTag);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void updateGithubInfo(String githubId, String avatarUrl, String githubUrl) {
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
    }

    public void updateUsername(String username) {
        if(username == null || username.isBlank()){
            throw new UserException(ErrorCode400.USERNAME_SHOULD_BE_NOT_EMPTY);
        }
        this.username = username;
    }

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void signUpComplete(){
        this.signupYn = true;
    }

    public void updateRegion(Region region) {
        if(region == null){
            throw new UserException(ErrorCode400.USER_REGION_SHOULD_BE_NOT_EMPTY);
        }
        this.region = region;
        this.regionAuthenticationAt = LocalDateTime.now();
    }

    //TODO : 추후 구현 필요
    public void decreaseJandiRate() {
        return;
    }
}
