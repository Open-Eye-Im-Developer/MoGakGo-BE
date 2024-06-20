package io.oeid.mogakgo.domain.outbox.infrastructure;

import static io.oeid.mogakgo.domain.outbox.domain.entity.QOutboxEvent.outboxEvent;
import static io.oeid.mogakgo.domain.log.domain.entity.QMessageLog.messageLog;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.outbox.domain.EventStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryCustomImpl implements OutboxRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<String> getProcessedEventId(String key) {

        // 만약, 같은 key를 가진 이벤트가 이미 발행된 적이 있다면, 즉, 컨슈머에서 한 번 소비된 적이 있다면
        Optional<String> result = Optional.ofNullable(jpaQueryFactory.select(outboxEvent.eventId)
            .from(outboxEvent)
            .innerJoin(messageLog)
            .on(outboxEvent.eventId.eq(messageLog.eventId))
            .where(
                outboxEvent.key.eq(key),
                outboxEvent.status.eq(EventStatus.PENDING)
            )
            .fetchOne());

        return result.isPresent() ? result : Optional.ofNullable(jpaQueryFactory.select(outboxEvent.eventId)
            .from(outboxEvent)
            .where(
                outboxEvent.key.eq(key),
                outboxEvent.status.eq(EventStatus.PENDING)
            )
            .orderBy(outboxEvent.createdAt.desc())
            .limit(1)
            .fetchOne());
    }
}
