package io.oeid.mogakgo.domain.review.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name = "review_tb")
@NoArgsConstructor(access = PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating")
    private ReviewRating rating;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Builder
    private Review(User sender, User receiver, Project project, ReviewRating rating) {
        validateUsers(sender, receiver);
        this.sender = sender;
        this.receiver = receiver;
        this.project = validateProject(project);
        this.rating = rating;
    }

    private void validateUsers(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new RuntimeException();
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException();
        }
    }

    private Project validateProject(Project project) {
        if (project == null) {
            throw new RuntimeException();
        }
        return project;
    }
}
