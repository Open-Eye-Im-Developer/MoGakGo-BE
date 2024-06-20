package io.oeid.mogakgo.domain.outbox.infrastructure;

import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("""
        select obe from OutboxEvent obe where obe.key = :key and obe.status = 'PENDING'
        order by obe.createdAt desc limit 1
    """)
    Optional<OutboxEvent> findByKey(String key);

    @Query("select obe from OutboxEvent obe where obe.eventId = :eventId")
    Optional<OutboxEvent> findByEventId(String eventId);
}
