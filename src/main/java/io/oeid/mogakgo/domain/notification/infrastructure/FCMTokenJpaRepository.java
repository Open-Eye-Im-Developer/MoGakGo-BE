package io.oeid.mogakgo.domain.notification.infrastructure;

import io.oeid.mogakgo.domain.notification.domain.vo.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenJpaRepository extends JpaRepository<FCMToken, Long> {

}