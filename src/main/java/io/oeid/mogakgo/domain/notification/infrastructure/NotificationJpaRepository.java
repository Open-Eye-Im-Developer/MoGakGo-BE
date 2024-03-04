package io.oeid.mogakgo.domain.notification.infrastructure;

import io.oeid.mogakgo.domain.notification.domain.Notification;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long>,
    NotificationCustomRepository {

    Optional<Notification> findByIdAndReceiver(Long id, User receiver);
}