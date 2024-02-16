package io.oeid.mogakgo.domain.project.domain.entity;

import io.oeid.mogakgo.common.base.BaseTimeEntity;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.domain.entity.vo.CreatorInfo;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Getter
@Entity
@Table(name = "project_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false)
    private User creator;

    @Embedded
    private CreatorInfo creatorInfo;

    @Embedded
    private MeetingInfo meetingInfo;

    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus;

    @Builder
    private Project(
        User creator, LocalDateTime meetStartTime, LocalDateTime meetEndTime, Point meetLocation,
        String meetDetail, Long mainAchievementId) {
        this.creator = creator;
        this.creatorInfo = CreatorInfo.of(creator, mainAchievementId);
        this.meetingInfo = MeetingInfo.of(meetStartTime, meetEndTime, meetLocation, meetDetail);
        this.projectStatus = ProjectStatus.PENDING;
    }

    public static Project of(User user, LocalDateTime meetStartTime, LocalDateTime meetEndTime,
        Point meetLocation, String meetDetail, Long mainAchievementId) {

        return Project.builder()
            .creator(user)
            .meetStartTime(meetStartTime)
            .meetEndTime(meetEndTime)
            .meetLocation(meetLocation)
            .meetDetail(meetDetail)
            .mainAchievementId(mainAchievementId)
            .build();
    }
}
