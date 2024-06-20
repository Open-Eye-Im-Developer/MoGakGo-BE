package io.oeid.mogakgo.domain.outbox.infrastructure;

import java.util.Optional;

public interface OutboxRepositoryCustom {

    Optional<String> getProcessedEventId(String key);

}
