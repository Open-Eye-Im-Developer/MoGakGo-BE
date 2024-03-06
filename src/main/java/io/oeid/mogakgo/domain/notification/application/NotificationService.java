package io.oeid.mogakgo.domain.notification.application;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.notification.application.dto.req.NotificationCreateRequest;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCheckedRes;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCreateResponse;
import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.domain.notification.infrastructure.NotificationJpaRepository;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserCommonService userCommonService;
    private final NotificationJpaRepository notificationRepository;

    @Transactional
    public NotificationCreateResponse createNotification(NotificationCreateRequest request) {
        log.info("createNotification request: {}", request);
        User sender = userCommonService.getUserById(request.getSenderId());
        User receiver = userCommonService.getUserById(request.getReceiverId());
        Notification notification = notificationRepository.save(request.toEntity(sender, receiver));
        return NotificationCreateResponse.from(notification);
    }

    public CursorPaginationResult<NotificationPublicApiRes> getNotifications(Long userId,
        CursorPaginationInfoReq pageable) {
        User user = userCommonService.getUserById(userId);
        return notificationRepository.findByUserIdWithPagination(user.getId(), pageable);
    }

    @Transactional
    public NotificationCheckedRes checkedNotification(Long userId, Long notificationId) {
        User user = userCommonService.getUserById(userId);
        Notification notification = notificationRepository.findByIdAndReceiver(notificationId, user)
            .orElseThrow(() -> new NotificationException(ErrorCode404.NOTIFICATION_NOT_FOUND));
        notification.markAsChecked();
        return new NotificationCheckedRes(notification.getId());
    }
}
