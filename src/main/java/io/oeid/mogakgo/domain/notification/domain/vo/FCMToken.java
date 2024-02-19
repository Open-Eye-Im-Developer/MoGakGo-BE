package io.oeid.mogakgo.domain.notification.domain.vo;

import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "fcm_token_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FCMToken {

    @Id
    @Column(name = "id")
    private Long userId;

    @Column(name = "token")
    private String token;

    public FCMToken(Long userId, String token) {
        this.userId = verifyUserId(userId);
        this.token = verifyToken(token);
    }

    public Long verifyUserId(Long userId) {
        if (userId == null) {
            throw new NotificationException(ErrorCode400.USER_ID_NOT_NULL);
        }
        return userId;
    }

    public String verifyToken(String fcmToken) {
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new NotificationException(ErrorCode400.NOTIFICATION_FCM_TOKEN_NOT_NULL);
        }
        return fcmToken;
    }

    public void updateToken(String token) {
        this.token = verifyToken(token);
    }
}
