package io.oeid.mogakgo.domain.notification.infrastructure;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long>,
    NotificationCustomRepository {

}