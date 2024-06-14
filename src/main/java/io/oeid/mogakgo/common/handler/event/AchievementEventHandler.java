package io.oeid.mogakgo.common.handler.event;

import static io.oeid.mogakgo.exception.code.ErrorCode404.OUTBOX_EVENT_NOT_FOUND;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.core.properties.kafka.MessageProducer;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.exception.OutboxException;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AchievementEventHandler {

    private static final String TOPIC = "achievement";

    private final OutboxJpaRepository outboxRepository;
    private final MessageProducer messageProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void executeEvent(final AchievementEvent event) {

        log.info("published event '{}' through thread '{}'", event, Thread.currentThread().getName());

        // TODO: 토픽에 어떤 메시지 형태로 컨슈머에게 전달할 것인지 고민
        messageProducer.sendMessage(TOPIC, Event.<AchievementEvent>builder()
            .event(event)
            .build()
        );

        // TODO: Outbox를 조회할 식별자를 어떻게 구성할지 고민✓
        OutboxEvent outbox = getRequestedEvent(generateKey(event));
        outbox.complete();

    }

    private OutboxEvent getRequestedEvent(String key) {
        return outboxRepository.findByKey(key)
            .orElseThrow(() -> new OutboxException(OUTBOX_EVENT_NOT_FOUND));
    }

    private String generateKey(final GeneralEvent event) {
        return event.getUserId().toString() + "-" + event.getActivityType().toString();
    }

}
