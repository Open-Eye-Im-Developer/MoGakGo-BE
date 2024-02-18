package io.oeid.mogakgo.domain.project.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_TAG_COUNT;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_FORBIDDEN_OPERATION;

import io.oeid.mogakgo.common.base.BaseTimeEntity;
import io.oeid.mogakgo.domain.project.domain.entity.enums.ProjectStatus;
import io.oeid.mogakgo.domain.project.domain.entity.vo.CreatorInfo;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.presentation.dto.req.ProjectTagCreateReq;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

@Getter
@Entity
@Table(name = "project_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false)
    private User creator;

    @Embedded
    private CreatorInfo creatorInfo;

    @Embedded
    private MeetingInfo meetingInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<ProjectTag> projectTags = new ArrayList<>();

    @Builder
    private Project(
        Long id, User creator, LocalDateTime meetStartTime,
        LocalDateTime meetEndTime, Double meetLat, Double meetLng,
        String meetDetail, Long mainAchievementId,
        List<ProjectTagCreateReq> projectTags
    ) {
        this.id = id;
        this.creator = creator;
        this.creatorInfo = CreatorInfo.of(creator, mainAchievementId);
        this.meetingInfo = MeetingInfo.of(meetStartTime, meetEndTime,
            new GeometryFactory().createPoint(new Coordinate(meetLng, meetLat)),
            meetDetail);
        this.projectStatus = ProjectStatus.PENDING;
        addProjectTagsWithValidation(projectTags);
    }

    public void delete(User tokenUser) {
        validateAvailableDelete(tokenUser);
        super.delete();
    }

    public void cancel(User tokenUser, boolean projectHasReq) {
        validateAvailableCancel(tokenUser);
        // 매칭이 되었거나, 매칭 준비중이지만 요청이 있을때는 잔디력 감소
        if (projectHasReq) {
            this.creator.decreaseJandiRate();
        }
        this.projectStatus = ProjectStatus.CANCELED;
    }

    private void validateAvailableCancel(User tokenUser) {
        validateCreator(tokenUser);
        this.projectStatus.validateAvailableCancel();
    }

    private void validateAvailableDelete(User tokenUser) {
        validateCreator(tokenUser);
        this.projectStatus.validateAvailableDelete();
    }

    private void validateCreator(User tokenUser) {
        if (tokenUser == null || !this.creator.getId().equals(tokenUser.getId())) {
            throw new ProjectException(PROJECT_FORBIDDEN_OPERATION);
        }
    }

    private void addProjectTagsWithValidation(List<ProjectTagCreateReq> projectTags) {
        projectTags.forEach(tag -> this.projectTags.add(
            ProjectTag.of(tag.getContent(), this))
        );
        validateTags();
    }

    private void validateTags() {
        if (projectTags.isEmpty() || projectTags.size() > 4) {
            throw new ProjectException(INVALID_PROJECT_TAG_COUNT);
        }
    }
}
