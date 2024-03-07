package io.oeid.mogakgo.domain.notification.application;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;
import io.oeid.mogakgo.domain.notification.domain.vo.FCMToken;
import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.domain.notification.infrastructure.FCMTokenJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FCMNotificationService {

    private final FCMTokenJpaRepository fcmTokenRepository;
    private final UserCommonService userCommonService;
    private final FirebaseMessaging firebaseMessaging;
    private final String clientUrl;

    public FCMNotificationService(FCMTokenJpaRepository fcmTokenRepository,
        UserCommonService userCommonService, FirebaseMessaging firebaseMessaging,
        @Value("${auth.client-url}") String clientUrl) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userCommonService = userCommonService;
        this.firebaseMessaging = firebaseMessaging;
        this.clientUrl = clientUrl;
    }

    @Transactional
    public void manageToken(Long userId, String fcmToken) {
        log.info("manageToken Start");
        FCMToken token = fcmTokenRepository.findById(userCommonService.getUserById(userId).getId())
            .orElseGet(() -> new FCMToken(userId, fcmToken));
        token.updateToken(fcmToken);
        fcmTokenRepository.save(token);
        log.info("manageToken End");
    }

    public void sendNotification(Long userId, String title, String body) {
        log.info("sendNotification Start");
        String fcmToken = getFCMToken(userId);
        // send notification
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(fcmToken)
            .build();
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message: " + e.getMessage());
        }
        log.info("sendNotification End");
    }

    public void sendNotification(Long userId, String title, String body, String redirectUri) {
        log.info("sendNotification Start");
        String fcmToken = getFCMToken(userId);
        WebpushConfig webpushConfig = WebpushConfig.builder()
            .setFcmOptions(WebpushFcmOptions.builder()
                .setLink(clientUrl + redirectUri)
                .build())
            .build();
        // send notification
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(fcmToken)
            .setWebpushConfig(webpushConfig)
            .build();
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message: " + e.getMessage());
        }
        log.info("sendNotification End");
    }


    private String getFCMToken(Long userId) {
        return fcmTokenRepository.findById(userId)
            .map(FCMToken::getToken)
            .orElseThrow(
                () -> new NotificationException(ErrorCode404.NOTIFICATION_FCM_TOKEN_NOT_FOUND));
    }
}
