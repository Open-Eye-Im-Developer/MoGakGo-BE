package io.oeid.mogakgo.domain.notification.application;

import static io.oeid.mogakgo.domain.notification.domain.enums.NotificationMessage.REVIEW_REQUEST_MESSAGE;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;
import io.oeid.mogakgo.domain.notification.domain.enums.FCMNotificationType;
import io.oeid.mogakgo.domain.notification.domain.vo.FCMToken;
import io.oeid.mogakgo.domain.notification.infrastructure.FCMTokenJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendNotification(Long userId, String title, String body) {
        getFCMToken(userId).ifPresent(
            fcmToken -> {
                Message message = generateFirebaseMessage(title, body, fcmToken.getToken());
                sendMessageToFCM(message);
            }
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendNotification(Long userId, String title, String body,
        FCMNotificationType notificationType) {
        getFCMToken(userId).ifPresent(
            fcmToken -> {
                Message message = generateFirebaseMessage(title, body,
                    notificationType.getRedirectUri(), fcmToken.getToken());
                sendMessageToFCM(message);
            }
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendNotification(Long userId, Long receiverId, Long projectId) {
        String redirectUrl =
            FCMNotificationType.REVIEW_REQUEST.getRedirectUri() + "?receiverId=" + receiverId
                + "&projectId=" + projectId;
        getFCMToken(userId).ifPresent(
            fcmToken -> {
                Message message = generateFirebaseMessage(REVIEW_REQUEST_MESSAGE.getTitle(),
                    REVIEW_REQUEST_MESSAGE.getMessage(), redirectUrl, fcmToken.getToken());
                sendMessageToFCM(message);
            }
        );
    }

    private void sendMessageToFCM(Message message) {
        log.info("sendNotification Start: {}", message);
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message: " + e.getMessage());
        }
    }

    private Message generateFirebaseMessage(String title, String body, String token) {
        return Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(token)
            .build();
    }

    private Message generateFirebaseMessage(String title, String body, String redirectUri,
        String token) {
        return Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setWebpushConfig(WebpushConfig.builder()
                .setFcmOptions(WebpushFcmOptions.builder()
                    .setLink(clientUrl + redirectUri)
                    .build())
                .build())
            .setToken(token)
            .build();
    }

    private Optional<FCMToken> getFCMToken(Long userId) {
        return fcmTokenRepository.findById(userId);
    }
}
