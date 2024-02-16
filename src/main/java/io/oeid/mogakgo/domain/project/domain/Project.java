package io.oeid.mogakgo.domain.project.domain;

import io.oeid.mogakgo.domain.project.domain.enums.ProjectStatus;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.geo.Point;

@Getter
@Entity
@Table(name = "project_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false)
    private User user;

    @Column(name = "user_github_id")
    private String userGithubId;

    @Column(name = "bio", length = 50)
    private String bio;

    @Column(name = "jandi_rating", nullable = false)
    private Double jandiRating;

    private String username;

    private String region;

    @Column(name = "meet_start_time")
    private LocalDateTime meetStartTime;

    @Column(name = "meet_end_time")
    private LocalDateTime meetEndTime;

    @Column(name = "meet_location")
    private Point meetLocation;

    @Column(name = "meet_detail")
    private String meetDetail;

    @Column(name = "main_achievement_id", nullable = false)
    private Long mainAchievementId;

    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Project(User user, LocalDateTime meetStartTime, LocalDateTime meetEndTime, Point meetLocation,
        String meetDetail, Long mainAchievementId) {

        validateMeetingTime(meetStartTime, meetEndTime);
        this.user = user;
        this.userGithubId = user.getGithubId();
        this.bio = user.getBio();
        this.jandiRating = user.getJandiRate();
        this.username = user.getUsername();
        this.region = setRegionAfterAuthentication(user);
        this.meetStartTime = meetStartTime;
        this.meetEndTime = meetEndTime;
        this.meetLocation = meetLocation;
        this.meetDetail = meetDetail;
        this.mainAchievementId = mainAchievementId;
        this.projectStatus = ProjectStatus.PENDING;
    }

    private String setRegionAfterAuthentication(User user) {
        if (user.getRegion() == null) {
            throw new RuntimeException("need to authenticate your neighborhood!");
        }
        return user.getRegion();
    }

    private static void validateMeetingTime(LocalDateTime meetStartTime, LocalDateTime meetEndTime) {
        LocalDate today = LocalDate.now();
        if (!meetStartTime.toLocalDate().equals(today) || !meetEndTime.toLocalDate().equals(today)) {
            throw new RuntimeException("meeting is only available on the same day!");
        }
        if (meetEndTime.isBefore(meetStartTime) || meetEndTime.isEqual(meetStartTime)) {
            throw new RuntimeException("endTime must be later than the startTime!");
        }
    }

    public static Project of(User user, LocalDateTime meetStartTime, LocalDateTime meetEndTime,
        Point meetLocation, String meetDetail, Long mainAchievementId) {

        return new Project(user, meetStartTime, meetEndTime, meetLocation, meetDetail, mainAchievementId);
    }
}
