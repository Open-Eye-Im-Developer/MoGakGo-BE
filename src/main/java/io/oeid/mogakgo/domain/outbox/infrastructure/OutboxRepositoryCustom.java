package io.oeid.mogakgo.domain.outbox.infrastructure;

import io.oeid.mogakgo.domain.outbox.domain.EventType;
import java.util.Optional;

public interface OutboxRepositoryCustom {

    Optional<String> getProcessedEventId(String key, EventType type);

}
