package io.oeid.mogakgo.domain.outbox.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_ACHIEVEMENT_TYPE;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
import io.oeid.mogakgo.core.properties.kafka.MessageProducer;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.outbox.domain.EventStatus;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.domain.entity.OutboxEvent;
import io.oeid.mogakgo.domain.outbox.infrastructure.OutboxJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private static final String FIRST_TOPIC = "achievement";
    private static final String SECOND_TOPIC = "notification";
    private static final String PREFIX = ":";

    private final OutboxJpaRepository outboxRepository;
    private final MessageProducer messageProducer;

    @Scheduled(cron = "0 30 23 * * *")
    public void firstProcess() {

        // 첫 번째 이벤트 발행부터 실패한 케이스에 대한 re-publishing
        List<OutboxEvent> failedEvents = outboxRepository.findByStatusAndType(EventStatus.PENDING, EventType.ACHIEVEMENT);
        republish(failedEvents, FIRST_TOPIC);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void secondProcess() {

        // 두 번째 이벤트 발행만 실패한 케이스에 대한 re-publishing
        List<OutboxEvent> failedEvents = outboxRepository.findByStatusAndType(EventStatus.PENDING, EventType.NOTIFICATION);
        republish(failedEvents, SECOND_TOPIC);
    }

    private void republish(List<OutboxEvent> events, String topic) {
        events.forEach(event -> {
            log.info("failed event with eventId '{}' is re-published to topic '{}'",
                event.getEventId(), topic);
            try {
                messageProducer.sendMessage(topic, generate(event));
            } catch (NoSuchFieldException e) {
                log.warn("This type '{}' is not a supported event type!", event.getType());
                throw new AchievementException(INVALID_ACHIEVEMENT_TYPE);
            }
        });
    }

    private String[] parse(String key) {
        return key.split(PREFIX);
    }

    private Event<?> generate(OutboxEvent event) throws NoSuchFieldException {
        String[] elem = parse(event.getKey());
        Long userId = Long.parseLong(elem[0]);
        ActivityType activityType = ActivityType.valueOf(elem[1]);

        switch (event.getType()) {
            case ACHIEVEMENT -> {
                AchievementEvent achievementEvent = AchievementEvent.builder()
                    .userId(userId)
                    .activityType(activityType)
                    .target(event.getTarget())
                    .build();

                return Event.<AchievementEvent>builder()
                    .id(event.getEventId())
                    .event(achievementEvent)
                    .build();
            }

            case NOTIFICATION -> {
                NotificationEvent notificationEvent = NotificationEvent.builder()
                    .userId(userId)
                    .activityType(activityType)
                    .target(event.getTarget())
                    .build();

                return Event.<NotificationEvent>builder()
                    .id(event.getEventId())
                    .event(notificationEvent)
                    .build();
            }

            default -> throw new NoSuchFieldException("This type is not a supported event type!");
        }
    }

}
