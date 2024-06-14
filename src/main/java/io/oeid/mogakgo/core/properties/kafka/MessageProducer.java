package io.oeid.mogakgo.core.properties.kafka;

import static io.oeid.mogakgo.exception.code.ErrorCode404.OUTBOX_EVENT_NOT_FOUND;

import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.exception.OutboxException;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {


    private final ThreadPoolExecutor executor;
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;
    private final OutboxJpaRepository outboxRepository;

    public void sendMessage(String topic, Event<?> event) {
        kafkaTemplate.send(topic, event)
            .whenComplete(
                (res, ex) -> {
                    if (ex != null) {
                        // handle the exception scenario

                        // TODO: 메시지 발행 실패에 대한 재처리 전략 구성 필요
                        log.error("Failed to send message due to '{}'", ex.getMessage());
                    } else if (res != null) {
                        // send data to db

                        log.info("Sent message '{}' through thread '{}'",
                            event, Thread.currentThread().getName()); // kafka-producer-network-thread | producer-1
                        log.info("Sent message '{}' with offset '{}' thorugh topic '{}'",
                            event, res.getRecordMetadata().offset(), topic);

                        // TODO: 디비 작업의 병목현상을 해결할 수 있는 방안 필요
                        executor.execute(process(res));
                    }
                }
            );
    }

    private Runnable process(SendResult<String, Event<?>> res) {
        return () -> {
            // TODO: Outbox를 조회할 식별자를 어떻게 구성할지 고민✓
            OutboxEvent outbox = getRequestedEvent(
                generateKey((GeneralEvent) res.getProducerRecord().value().getEvent())
            );
            outbox.complete();
        };
    }

    private OutboxEvent getRequestedEvent(String key) {
        return outboxRepository.findByKey(key)
            .orElseThrow(() -> new OutboxException(OUTBOX_EVENT_NOT_FOUND));
    }

    private String generateKey(final GeneralEvent event) {
        return event.getUserId().toString() + event.getActivityType().toString();
    }

    public void sendMessage(String topic, String key, Event<?> event) {
        kafkaTemplate.send(topic, key, event);
    }

}
