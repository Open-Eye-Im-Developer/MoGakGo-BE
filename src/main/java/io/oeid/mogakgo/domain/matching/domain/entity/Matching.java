package io.oeid.mogakgo.domain.matching.domain.entity;

import static io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus.PROGRESS;
import static io.oeid.mogakgo.exception.code.ErrorCode403.MATCHING_FORBIDDEN_OPERATION;

import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "matching_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_id", updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", updatable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status")
    private MatchingStatus matchingStatus;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    private Matching(
        Long id, Project project, User sender
    ) {
        this.id = id;
        this.project = project;
        this.sender = sender;
        this.matchingStatus = PROGRESS;
    }

    // TODO: 프로젝트 상태 변경과 매칭 상태 변경을 동시에 처리하는 것을 보장하는것이 좋을 것 같다.
    public void cancel(User tokenUser) {
        validateAvailableCancel(tokenUser);
        project.finish();
        this.matchingStatus = MatchingStatus.CANCELED;
    }

    private void validateAvailableCancel(User tokenUser) {
        validateParticipants(tokenUser);
        matchingStatus.validateAvailableCancel();
    }

    public void validateParticipants(User tokenUser) {
        var participants = List.of(project.getCreator().getId(), sender.getId());

        if (!participants.contains(tokenUser.getId())) {
            throw new MatchingException(MATCHING_FORBIDDEN_OPERATION);
        }
    }

}
