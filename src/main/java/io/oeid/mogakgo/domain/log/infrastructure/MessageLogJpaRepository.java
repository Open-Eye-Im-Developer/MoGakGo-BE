package io.oeid.mogakgo.domain.log.infrastructure;

import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageLogJpaRepository extends JpaRepository<MessageLog, Long> {

    boolean existsByEventId(String eventId);
    void deleteByEventId(String eventId);
}
