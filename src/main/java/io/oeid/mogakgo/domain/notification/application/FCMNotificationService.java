package io.oeid.mogakgo.domain.notification.application;

import io.oeid.mogakgo.domain.notification.domain.vo.FCMToken;
import io.oeid.mogakgo.domain.notification.infrastructure.FCMTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMNotificationService {

    private final FCMTokenJpaRepository fcmTokenRepository;

    public void manageToken(Long userId, String fcmToken) {
        FCMToken token = fcmTokenRepository.findById(userId)
            .orElseGet(() -> new FCMToken(userId, fcmToken));
        token.updateToken(fcmToken);
        fcmTokenRepository.save(token);
    }
}
