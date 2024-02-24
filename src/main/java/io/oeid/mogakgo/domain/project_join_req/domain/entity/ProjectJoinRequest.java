package io.oeid.mogakgo.domain.project_join_req.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_CREATOR_PROJECT_JOIN_REQUEST;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_JOIN_REQUEST_REGION;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROJECT_NOT_FOUND;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.project_join_req.exception.ProjectJoinRequestException;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "project_join_request_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProjectJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", updatable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_request_status")
    private RequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", updatable = false)
    private Project project;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
  
    @Builder
    private ProjectJoinRequest(User sender, Project project) {
        this.sender = sender;
        this.project = project;
        this.requestStatus = RequestStatus.PENDING;
        validateSender(sender);
        validateProject(project);
        validateProjectCreator();
        validateUserRegion();
    }

    public static ProjectJoinRequest of(User sender, Project project) {
        return new ProjectJoinRequest(sender, project);
    }

    public void cancel(User user) {
        validateAvailableCancel(user);
        requestStatus = RequestStatus.CANCELED;
    }

    // TODO: 매칭 생성과 요청 수락이 동시에 일어날것이란 보장
    public void accept(User user) {
        validateAvailableAccept();
        project.match(user);
        requestStatus = RequestStatus.ACCEPTED;
    }

    private void validateAvailableAccept() {
        // 프로젝트 요청 상태 유효성 검증
        requestStatus.validateAvailableAccept();
    }

    private void validateAvailableCancel(User user) {
        validateSender(user);
        requestStatus.validateAvailableCancel();
    }

    private void validateSender(User user) {
        if (!sender.getId().equals(user.getId())) {
            throw new ProjectJoinRequestException(PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION);
        }
    }

    private void validateProjectCreator() {
        if (project.getCreator().getId().equals(sender.getId())) {
            throw new ProjectJoinRequestException(INVALID_CREATOR_PROJECT_JOIN_REQUEST);
        }
    }

    private void validateUserRegion() {
        if (!project.getCreatorInfo().getRegion().equals(sender.getRegion())) {
            throw new ProjectJoinRequestException(INVALID_PROJECT_JOIN_REQUEST_REGION);
        }
    }

    private void validateProject(Project project) {
        if (!this.project.getId().equals(project.getId())) {
            throw new ProjectException(PROJECT_NOT_FOUND);
        }
        this.project.validateAvailableMatched();
    }
}
