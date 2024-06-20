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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@Transactional("transactionManager")
@RequiredArgsConstructor
public class AchievementEventHandler {

    private static final String TOPIC = "achievement";

    private final MessageProducer messageProducer;
    private final OutboxJpaRepository outboxRepository;

    // TODO: 'Achievement' 'Notification' 에 대해 각각의 비동기 스레드 처리를 위한 설정 추가
    @Async // 비동기로 호출, 즉 별도의 tx에서 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void executeEvent(final AchievementEvent event) {

        OutboxEvent outbox = getRequestedEvent(generateKey(event));
        Event<AchievementEvent> message = wrap(event, outbox);

        // SimpleAsyncTaskExecutor-1
        log.info("published event '{}' with messageId '{}' through thread '{}'",
            event, message.getId(), Thread.currentThread().getName());

        // TODO: 토픽에 어떤 메시지 형태로 컨슈머에게 전달할 것인지 고민
        messageProducer.sendMessage(TOPIC, message);

    }

    private String generateKey(final GeneralEvent event) {
        return event.getUserId().toString() + event.getActivityType().toString();
    }

    private OutboxEvent getRequestedEvent(String key) {
        return outboxRepository.findByKey(key)
            .orElseThrow(() -> new OutboxException(OUTBOX_EVENT_NOT_FOUND));
    }

    private Event<AchievementEvent> wrap(AchievementEvent event, OutboxEvent outbox) {
        return Event.<AchievementEvent>builder()
            .id(outbox.getEventId())
            .event(event)
            .build();
    }

}
