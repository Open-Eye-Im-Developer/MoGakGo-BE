package io.oeid.mogakgo.common.handler.event;


import static io.oeid.mogakgo.exception.code.ErrorCode404.OUTBOX_EVENT_NOT_FOUND;

import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
import io.oeid.mogakgo.core.properties.kafka.MessageProducer;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.exception.OutboxException;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional(value = "transactionManager", readOnly = true)
@RequiredArgsConstructor
public class NotificationEventHandler {

    private static final String TOPIC = "notification";

    private final MessageProducer messageProducer;
    private final OutboxJpaRepository outboxRepository;

    @Async
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void executeEvent(final NotificationEvent event) {

        String eventId = getRequestedEventId(generateKey(event));
        Event<NotificationEvent> message = wrap(event, eventId);

        log.info("published event '{}' with messageId '{}' to topic '{}' through thread '{}",
            event, message.getId(), TOPIC, Thread.currentThread().getName());

        messageProducer.sendMessage(TOPIC, message);

    }

    private String generateKey(final GeneralEvent event) {
        return event.getUserId().toString() + ":" + event.getActivityType().toString();
    }

    private String getRequestedEventId(String key) {
        return outboxRepository.getProcessedEventId(key, EventType.NOTIFICATION)
            .orElseThrow(() -> new OutboxException(OUTBOX_EVENT_NOT_FOUND));
    }

    private Event<NotificationEvent> wrap(NotificationEvent event, String eventId) {
        return Event.<NotificationEvent>builder()
            .id(eventId)
            .event(event)
            .build();
    }

}
