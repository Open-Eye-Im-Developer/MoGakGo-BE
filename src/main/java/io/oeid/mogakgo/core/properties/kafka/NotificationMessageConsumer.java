package io.oeid.mogakgo.core.properties.kafka;


import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
// TODO: 인터페이스 도입 고려
public class NotificationMessageConsumer {

    private static final String TOPIC = "notification";

    private final AchievementFacadeService achievementFacadeService;
    private final AchievementProgressService achievementProgressService;
    private final NotificationService notificationService;

    @KafkaListener(topics = TOPIC, groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    protected void consumeNotification(ConsumerRecord<String, Event<NotificationEvent>> record,
        Acknowledgment acknowledgment) {

        Event<NotificationEvent> event = record.value();
        log.info("receive event '{}' from producer through topic 'notification'", event);

        NotificationEvent notificationEvent = event.getEvent();
        process(notificationEvent);

        acknowledgment.acknowledge();
    }

    private void process(final NotificationEvent event) {

        // 사용자가 현재 달성할 수 있는 업적 ID
        Long achievementId = validAchievementId(event);

        if (achievementId != null) {

            Achievement achievement = achievementFacadeService.getById(achievementId);
            Object progressCount = event.getTarget() == null
                ? getProgressCountForAchievement(event.getUserId(), achievement) + 1
                : event.getTarget();

            // -- 업적이 달성 가능한 조건을 만족했을 경우
            if (validateAvailabilityToAchieve(progressCount, achievement)) {
                notificationService.createAchievementNotification(event.getUserId(), achievement);
            }
        }
    }

    private Long validAchievementId(final GeneralEvent event) {
        return achievementFacadeService.getAvailableAchievementId(
            event.getUserId(), event.getActivityType()
        );
    }

    private Integer getProgressCountForAchievement(Long userId, Achievement achievement) {
        if (achievement.getRequirementType().equals(RequirementType.ACCUMULATE)) {
            return achievementProgressService.getAccumulatedProgressCount(userId, achievement.getActivityType());
        }
        return achievementProgressService.getProgressCountMapWithoutToday(userId,
            List.of(achievement.getActivityType())).get(achievement.getActivityType());
    }

    private boolean validateAvailabilityToAchieve(Object target, Achievement achievement) {
        if (target instanceof Integer) {
            return Objects.equals(achievement.getRequirementValue(), target);
        } else if (target instanceof Double) {
            return achievement.getRequirementValue() <= (Double) target;
        } else {
            throw new IllegalArgumentException("Unsupported target type");
        }
    }

}
