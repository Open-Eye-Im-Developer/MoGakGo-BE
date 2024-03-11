package io.oeid.mogakgo.domain.notification.domain;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.notification.domain.enums.NotificationMessage;
import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "notification_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private NotificationTag notificationTag;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "checked_yn")
    private Boolean checkedYn;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Notification(String message, User user, User receiver, Project project) {
        this.notificationTag = NotificationTag.REVIEW_REQUEST;
        this.user = validateUser(user);
        this.message = message;
        this.receiver = validateReceiver(user, receiver);
        this.project = validateProject(project);
    }

    private Notification(String message, User user, Achievement achievement) {
        this.notificationTag = NotificationTag.ACHIEVEMENT;
        this.user = validateUser(user);
        this.message = message;
        this.achievement = validateAchievement(achievement);
    }

    private Notification(String message, User user) {
        this.notificationTag = NotificationTag.REQUEST_ARRIVAL;
        this.user = validateUser(user);
        this.message = message;
    }

    private Notification(NotificationTag notificationTag, String message, User user,
        Project project) {
        this.notificationTag = validateNotificationTag(notificationTag);
        this.user = validateUser(user);
        this.message = message;
        this.project = validateProject(project);
    }

    public static Notification newReviewRequestNotification(User user, User receiver,
        Project project) {
        return new Notification(
            receiver.getUsername() + NotificationMessage.REVIEW_REQUEST_MESSAGE.getMessage(), user,
            receiver, project);
    }

    public static Notification newAchievementNotification(User user, Achievement achievement) {
        return new Notification(
            achievement.getTitle() + NotificationMessage.ACHIEVEMENT_MESSAGE.getMessage(), user,
            achievement);
    }

    public static Notification newRequestArrivalNotification(User user) {
        return new Notification(NotificationMessage.REQUEST_ARRIVAL_MESSAGE.getMessage(), user);
    }

    public static Notification newMatchingSuccessNotification(User user, Project project) {
        return new Notification(NotificationTag.MATCHING_SUCCEEDED,
            NotificationMessage.MATCHING_SUCCESS_MESSAGE.getMessage(), user, project);
    }

    public static Notification newMatchingFailedNotification(User user, Project project) {
        return new Notification(NotificationTag.MATCHING_FAILED,
            NotificationMessage.MATCHING_FAILED_MESSAGE.getMessage(), user, project);
    }

    private NotificationTag validateNotificationTag(NotificationTag notificationTag) {
        if (!notificationTag.equals(NotificationTag.MATCHING_SUCCEEDED) && !notificationTag.equals(
            NotificationTag.MATCHING_FAILED)) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_TAG_SHOULD_LIKE_MATCHING);
        }
        return notificationTag;
    }

    private User validateUser(User user) {
        if (user == null) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_USER_NOT_NULL);
        }
        return user;
    }

    private User validateReceiver(User user, User receiver) {
        if (receiver == null) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_RECEIVER_NOT_NULL);
        }
        if (user.equals(receiver)) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_USER_RECEIVER_DUPLICATED);
        }
        return receiver;
    }

    private Project validateProject(Project project) {
        if (project == null) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_PROJECT_NOT_NULL);
        }
        return project;
    }

    private Achievement validateAchievement(Achievement achievement) {
        if (achievement == null) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_ACHIEVEMENT_NOT_NULL);
        }
        return achievement;
    }

    public void markAsChecked() {
        this.checkedYn = true;
    }
}
