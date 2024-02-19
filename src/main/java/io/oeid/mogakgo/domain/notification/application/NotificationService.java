package io.oeid.mogakgo.domain.notification.application;

import io.oeid.mogakgo.domain.notification.application.dto.req.NotificationCreateRequest;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCreateResponse;
import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.infrastructure.NotificationJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
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
        User user = userCommonService.getUserById(request.getUserId());
        Notification notification = notificationRepository.save(request.toEntity(user));
        return NotificationCreateResponse.from(notification);
    }
}
