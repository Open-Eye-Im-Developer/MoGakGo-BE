package io.oeid.mogakgo.core.properties.kafka;

import static io.oeid.mogakgo.exception.code.ErrorCode404.OUTBOX_EVENT_NOT_FOUND;

import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.exception.OutboxException;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final ThreadPoolTaskExecutor outboxTaskExecutor;
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;
    private final OutboxJpaRepository outboxRepository;

    public void sendMessage(String topic, Event<?> event) {
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(topic, event)
                .whenComplete(
                    (res, ex) -> {
                        if (ex != null) {
                            // handle the exception scenario
                            // fallback: re-publishing failed message or event using outboxProcessor
                            log.error("Failed to send message due to '{}'", ex.getMessage());
                        } else if (res != null) {
                            // send data to db
                            log.info("Sent message '{}' through thread '{}'",
                                event, Thread.currentThread().getName()); // kafka-producer-network-thread | producer-1
                            log.info("Sent message '{}' with id '{}' and offset '{}' completely thorugh topic '{}'",
                                event, event.getId(), res.getRecordMetadata().offset(), topic);

                            // message is already committed in broker!
                            // if execution fail itself, re-publishing processed message,
                            // and then, consumer will verify duplication!
                            outboxTaskExecutor.execute(process(res));
                        }
                    }
                );
            return true;
        });
    }

    private Runnable process(SendResult<String, Event<?>> res) {
        return () -> {
            log.info("status of message published successfully will be updated to 'COMPLETE' thorugh thread '{}'",
                Thread.currentThread().getName());

            // use threadPoolTaskExecutor to avoid bottleneck for db insert task!
            // if threadPoolTaskExecutor is full, caller thread will execute task
            OutboxEvent outbox = getRequestedEvent(res.getProducerRecord().value().getId());
            outbox.complete();
        };
    }

    private OutboxEvent getRequestedEvent(String eventId) {
        return outboxRepository.findByEventId(eventId)
            .orElseThrow(() -> new OutboxException(OUTBOX_EVENT_NOT_FOUND));
    }

    public void sendMessage(String topic, String key, Event<?> event) {
        kafkaTemplate.send(topic, key, event);
    }

}
