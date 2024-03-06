package io.oeid.mogakgo.domain.notification.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.NotificationSwagger;
import io.oeid.mogakgo.domain.notification.application.FCMNotificationService;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCheckedRes;
import io.oeid.mogakgo.domain.notification.presentation.dto.req.FCMTokenApiRequest;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController implements NotificationSwagger {

    private final FCMNotificationService fcmNotificationService;
    private final NotificationService notificationService;

    @PostMapping("/fcm")
    public ResponseEntity<Void> manageFCMToken(@UserId Long userId, @RequestBody @Valid
    FCMTokenApiRequest request) {
        fcmNotificationService.manageToken(userId, request.getFcmToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CursorPaginationResult<NotificationPublicApiRes>> getByUserId(
        @UserId Long id, @Valid @ModelAttribute CursorPaginationInfoReq pageable) {
        return ResponseEntity.ok().body(notificationService.getNotifications(id, pageable));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationCheckedRes> markCheckedNotification(@UserId Long userId,
        @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.checkedNotification(userId, notificationId));
    }
}
