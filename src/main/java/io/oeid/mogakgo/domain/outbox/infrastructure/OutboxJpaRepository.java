package io.oeid.mogakgo.domain.outbox.infrastructure;

import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxEvent, Long> {

}
