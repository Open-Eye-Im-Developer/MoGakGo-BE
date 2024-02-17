package io.oeid.mogakgo.domain.notification.domain;

import io.oeid.mogakgo.domain.notification.domain.enums.NotificationTag;
import io.oeid.mogakgo.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name = "notification_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private NotificationTag notificationTag;

    @Column(name = "detail_data")
    private String detailData;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Notification(User user, NotificationTag notificationTag, String detailData) {
        this.user = validateUser(user);
        this.notificationTag = validateNotificationTag(notificationTag);
        this.detailData = validateDetailData(detailData);
    }

    public static Notification of(User user, NotificationTag notificationTag, String detail) {
        return new Notification(user, notificationTag, detail);
    }

    private User validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        return user;
    }

    private NotificationTag validateNotificationTag(NotificationTag notificationTag) {
        if (notificationTag == null) {
            throw new IllegalArgumentException("notificationTag must not be null");
        }
        return notificationTag;
    }

    private String validateDetailData(String detailData) {
        if (detailData == null || detailData.isEmpty()) {
            throw new IllegalArgumentException("detailData must not be null or empty");
        }
        return detailData;
    }
}
