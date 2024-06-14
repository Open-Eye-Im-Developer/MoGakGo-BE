package io.oeid.mogakgo.common.handler.event;

import static io.oeid.mogakgo.exception.code.ErrorCode404.OUTBOX_EVENT_NOT_FOUND;

import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
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
public class NotificationEventHandler {

    private static final String TOPIC = "notification";

    private final MessageProducer messageProducer;
    private final OutboxJpaRepository outboxRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void executeEvent(final NotificationEvent event) {
        messageProducer.sendMessage(TOPIC, Event.<NotificationEvent>builder()
            .event(event)
            .build()
        );

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
