package io.oeid.mogakgo.domain.notification.infrastructure;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import java.util.List;

public interface NotificationCustomRepository {

    List<Notification> findByUserIdWithPagination(Long userId, Long cursorId, int pageSize);
}
