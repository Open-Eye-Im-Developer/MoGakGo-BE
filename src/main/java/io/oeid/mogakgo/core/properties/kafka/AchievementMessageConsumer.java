package io.oeid.mogakgo.core.properties.kafka;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_ACHIEVEMENT_TYPE;
import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;

import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.GeneralEvent;
import io.oeid.mogakgo.domain.achievement.application.AchievementFacadeService;
import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.log.application.DuplicateLogService;
import io.oeid.mogakgo.domain.notification.application.NotificationEventHelper;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
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
@RequiredArgsConstructor
public class AchievementMessageConsumer {

    private static final String TOPIC = "achievement";
    private static final Integer MIN_PROGRESS_SIZE = 1;

    private final UserCommonService userCommonService;
    private final AchievementJpaRepository achievementRepository;
    private final AchievementFacadeService achievementFacadeService;
    private final UserAchievementJpaRepository userAchievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final AchievementProgressService achievementProgressService;
    private final NotificationEventHelper eventHelper;
    private final DuplicateLogService duplicateChecker;

    @KafkaListener(topics = TOPIC, groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    protected void consumeAchievement(List<ConsumerRecord<String, Event<AchievementEvent>>> records,
        Acknowledgment acknowledgment) {

        for (ConsumerRecord<String, Event<AchievementEvent>> record : records) {
            process(record);
        }

        // 메시지 소비가 성공적으로 처리되면, 브로커에게 커밋 요청
        acknowledgment.acknowledge();
    }

    @Transactional("transactionManager")
    public void process(ConsumerRecord<String, Event<AchievementEvent>> record) {
        String eventId = record.value().getId();
        if (duplicateChecker.isMessageIdProcessed(eventId)) {
            log.info("this message with eventId'{}' is already processing! no-caching!", eventId);
        } else {
            consume(record);
            duplicateChecker.caching(eventId);
        }
    }

    @Transactional("transactionManager")
    public void consume(ConsumerRecord<String, Event<AchievementEvent>> record) {

        Event<AchievementEvent> event = record.value();
        log.info("receive event '{}' with offset '{}' from producer through topic '{}'",
            event, record.offset(), record.topic());

        AchievementEvent achievementEvent = event.getEvent();

        try {
            process(achievementEvent);
        } catch (NoSuchFieldException e) {
            // handle to ex
            // TODO: Swagger에 추가
            log.warn("This type '{}' is not a supported event type!", event.getEventType());
            throw new AchievementException(INVALID_ACHIEVEMENT_TYPE);
        }

        eventHelper.publishEvent(achievementEvent);
    }

    @Transactional("transactionManager")
    public void process(final AchievementEvent event) throws NoSuchFieldException {

        // 사용자가 현재 달성할 수 있는 업적 ID
        Long achievementId = validAchievementId(event);

        if (achievementId != null) {

            User user = userCommonService.getUserById(event.getUserId());
            Achievement achievement = achievementFacadeService.getById(achievementId);
            Boolean isUnderway = achievementFacadeService.validateAlreadyInProgress(
                event.getUserId(), achievementId);
            Object progressCount = event.getTarget() == null
                ? getProgressCountForAchievement(event.getUserId(), achievement) + 1
                : event.getTarget();

            switch (achievement.getRequirementType()) {
                case ACCUMULATE -> {
                    // -- 이력 저장
                    saveActivity(event, user);

                    // -- 최초 업적 진행중
                    // -- 전체 달성 조건은 만족하지 못했지만, 진행 중인 경우 (예: 프로젝트 생성 5번 중 1번 진행)
                    achieveAtFirstTime(isUnderway, user, achievement);

                    // -- 업적이 달성 가능한 조건을 만족했을 경우
                    if (validateAvailabilityToAchieve(progressCount, achievement)) {

                        UserAchievement userAchievement = getByUserAndAchievement(
                            event.getUserId(), achievementId);
                        userAchievement.updateCompleted();
                    }
                }

                case SEQUENCE -> {
                    // -- 연속 달성 업적에 대해, 당일 업적 이벤트를 아직 발행한 적 없는 경우
                    // -- 이력 저장
                    if (achievementFacadeService.validateDuplicateActivityHistory(
                        event.getUserId(), event.getActivityType())) {
                        saveActivity(event, user);
                    }

                    // -- 최초 업적 진행중
                    // -- 전체 달성 조건은 만족하지 못했지만, 진행 중인 경우 (예: 프로젝트 생성 5번 중 1번 진행)
                    achieveAtFirstTime(isUnderway, user, achievement);

                    // -- 업적이 달성 가능한 조건을 만족했을 경우
                    if (validateAvailabilityToAchieve(progressCount, achievement)) {

                        UserAchievement userAchievement = getByUserAndAchievement(
                            event.getUserId(), achievementId);
                        userAchievement.updateCompleted();

                        // -- 저장된 이력을 삭제
                        // TODO: 바로 직전에 저장된 이력이 삭제되는지 테스트
                        List<UserActivity> activityHistory = userActivityRepository
                            .getHistoryByActivityType(
                                event.getUserId(),
                                event.getActivityType(),
                                achievement.getRequirementValue()
                            );
                        activityHistory.forEach(UserActivity::delete);
                    }
                }

                default -> throw new NoSuchFieldException("This type is not a supported event type!");
            }
        }
    }

    private Long validAchievementId(final GeneralEvent event) {
        return achievementFacadeService.getAvailableAchievementId(
            event.getUserId(), event.getActivityType()
        );
    }

    private void saveActivity(final AchievementEvent event, User user) {
        userActivityRepository.save(UserActivity.builder()
            .user(user)
            .activityType(event.getActivityType())
            .build()
        );
    }

    private void achieveAtFirstTime(Boolean isUnderway, User user, Achievement achievement) {
        if (isUnderway.equals(Boolean.FALSE)) {
            userAchievementRepository.save(UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .completed(Boolean.FALSE)
                .build()
            );
        }
    }

    private boolean canAchieveAtOnce(ActivityType activityType) {
        return getProgressLevel(activityType).equals(MIN_PROGRESS_SIZE);
    }

    private Integer getProgressLevel(ActivityType activityType) {
        return achievementRepository.findMaxProgressLevelByActivityType(activityType);
    }

    private String generateKey(final AchievementEvent event) {
        return event.getUserId().toString() + event.getActivityType().toString();
    }

    private UserAchievement getByUserAndAchievement(Long userId, Long achievementId) {
        return userAchievementRepository.findByUserAndAchievementId(userId, achievementId)
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
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
