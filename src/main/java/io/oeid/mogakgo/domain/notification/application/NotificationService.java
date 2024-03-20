package io.oeid.mogakgo.domain.notification.application;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.notification.application.dto.res.NotificationCheckedRes;
import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.notification.exception.NotificationException;
import io.oeid.mogakgo.domain.notification.infrastructure.NotificationJpaRepository;
import io.oeid.mogakgo.domain.notification.presentation.dto.res.NotificationPublicApiRes;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
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
@Transactional
public class NotificationService {

    private final UserCommonService userCommonService;
    private final NotificationJpaRepository notificationRepository;
    private final ProjectJpaRepository projectRepository;

    public void createReviewRequestNotification(Long userId, Long receiverId, Long projectId) {
        log.info("createReviewRequestNotification userId: {}, receiver: {}, project: {}", userId,
            receiverId, projectId);
        User user = userCommonService.getUserById(userId);
        User receiver = userCommonService.getUserById(receiverId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotificationException(ErrorCode404.PROJECT_NOT_FOUND));
        notificationRepository.save(
            Notification.newReviewRequestNotification(user, receiver, project));
    }

    public void createAchievementNotification(Long userId, Achievement achievement) {
        log.info("createAchievementNotification userId: {}, achievement: {}", userId,
            achievement.getId());
        User user = userCommonService.getUserById(userId);
        notificationRepository.save(Notification.newAchievementNotification(user, achievement));
    }

    public void createRequestArrivalNotification(Long userId) {
        log.info("createRequestArrivalNotification userId: {}", userId);
        User user = userCommonService.getUserById(userId);
        notificationRepository.save(Notification.newRequestArrivalNotification(user));
    }

    public void createMatchingSuccessNotification(Long userId, Project project) {
        log.info("createMatchingSuccessNotification userId: {}, project: {}", userId,
            project.getId());
        User user = userCommonService.getUserById(userId);
        notificationRepository.save(Notification.newMatchingSuccessNotification(user, project));
    }

    public void createMatchingFailedNotification(Long userId, Project project) {
        log.info("createMatchingFailedNotification userId: {}, project: {}", userId,
            project.getId());
        User user = userCommonService.getUserById(userId);
        notificationRepository.save(Notification.newMatchingFailedNotification(user, project));
    }

    @Transactional(readOnly = true)
    public CursorPaginationResult<NotificationPublicApiRes> getNotifications(Long userId,
        CursorPaginationInfoReq pageable) {
        User user = userCommonService.getUserById(userId);
        var result = notificationRepository.findByUserIdWithPagination(user.getId(),
            pageable.getCursorId(), pageable.getPageSize());
        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(
            result.stream().map(NotificationPublicApiRes::from).toList(),
            pageable.getPageSize());
    }

    public NotificationCheckedRes checkedNotification(Long userId, Long notificationId) {
        User user = userCommonService.getUserById(userId);
        Notification notification = notificationRepository.findByIdAndReceiver(notificationId, user)
            .orElseThrow(() -> new NotificationException(ErrorCode404.NOTIFICATION_NOT_FOUND));
        notification.markAsChecked();
        return new NotificationCheckedRes(notification.getId());
    }
}
