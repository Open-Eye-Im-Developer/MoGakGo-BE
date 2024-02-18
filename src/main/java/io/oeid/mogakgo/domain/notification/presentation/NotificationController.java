package io.oeid.mogakgo.domain.notification.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.domain.notification.application.FCMNotificationService;
import io.oeid.mogakgo.domain.notification.presentation.dto.req.FCMTokenApiRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final FCMNotificationService fcmNotificationService;

    @PostMapping("/fcm")
    public ResponseEntity<Void> manageFCMToken(@UserId Long userId, @RequestBody @Valid
    FCMTokenApiRequest request) {
        fcmNotificationService.manageToken(userId, request.getFcmToken());
        return ResponseEntity.ok().build();
    }


}
