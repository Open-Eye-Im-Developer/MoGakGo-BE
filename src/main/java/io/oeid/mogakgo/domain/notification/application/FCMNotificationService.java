package io.oeid.mogakgo.domain.notification.application;

import io.oeid.mogakgo.domain.notification.domain.vo.FCMToken;
import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.domain.notification.infrastructure.FCMTokenJpaRepository;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FCMNotificationService {

    private final FCMTokenJpaRepository fcmTokenRepository;

    @Transactional
    public void manageToken(Long userId, String fcmToken) {
        FCMToken token = fcmTokenRepository.findById(userId)
            .orElseGet(() -> new FCMToken(userId, fcmToken));
        token.updateToken(fcmToken);
        fcmTokenRepository.save(token);
    }

    private String getFCMToken(Long userId) {
        return fcmTokenRepository.findById(userId)
            .map(FCMToken::getToken)
            .orElseThrow(
                () -> new NotificationException(ErrorCode404.NOTIFICATION_FCM_TOKEN_NOT_FOUND));
    }
}
