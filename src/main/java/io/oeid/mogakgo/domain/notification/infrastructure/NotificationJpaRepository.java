package io.oeid.mogakgo.domain.notification.infrastructure;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

}