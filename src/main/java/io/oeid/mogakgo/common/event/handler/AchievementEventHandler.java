package io.oeid.mogakgo.common.event.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;
import static io.oeid.mogakgo.exception.code.ErrorCode404.ACHIEVEMENT_NOT_FOUND;

import io.oeid.mogakgo.common.event.AccumulateAchievementEvent;
import io.oeid.mogakgo.common.event.AccumulateAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.AchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementEvent;
import io.oeid.mogakgo.common.event.SequenceAchievementUpdateEvent;
import io.oeid.mogakgo.common.event.UserActivityEvent;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementEventHandler {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementJpaRepository achievementRepository;
    private final UserActivityJpaRepository userActivityRepository;
    private final UserCommonService userCommonService;

    @EventListener
    public void executeActivity(final UserActivityEvent event) {
        User user = userCommonService.getUserById(event.getUserId());
        userActivityRepository.save(UserActivity.builder()
            .user(user)
            .activityType(event.getActivityType())
            .build());
    }

    @EventListener
    public void executeEvent(final SequenceAchievementEvent event) {
        User user = userCommonService.getUserById(event.getUserId());
        Achievement achievement = getById(event.getAchievementId());

        userAchievementRepository.save(
            UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .completed(event.getCompleted())
                .build()
        );
    }

    @EventListener
    public void executeEvent(final AccumulateAchievementEvent event) {
        User user = userCommonService.getUserById(event.getUserId());
        Achievement achievement = getById(event.getAchievementId());

        userAchievementRepository.save(
            UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .completed(event.getCompleted())
                .build()
        );
    }

    @EventListener
    public void executeEvent(final AccumulateAchievementUpdateEvent event) {

        // 진행중인 업적에 대해 '달성' 업데이트
        UserAchievement userAchievement = getByUserAndAchievementId(event);
        userAchievement.updateCompleted();
    }

    @EventListener
    public void executeEvent(final SequenceAchievementUpdateEvent event) {

        // 진행중인 업적에 대해 '달성' 업데이트
        UserAchievement userAchievement = getByUserAndAchievementId(event);
        userAchievement.updateCompleted();

        // -- 'SEQUENCE' 타입 업적에 한해, 달성 조건을 위해 사용된 히스토리 soft delete 처리
        Achievement achievement = getById(event.getAchievementId());
        List<UserActivity> history = userActivityRepository.getActivityHistoryByActivityType(
            event.getUserId(), achievement.getActivityType(), achievement.getRequirementValue());
        history.forEach(UserActivity::delete);
    }

    public Achievement getById(Long achievementId) {
        return achievementRepository.findById(achievementId)
            .orElseThrow(() -> new AchievementException(ACHIEVEMENT_NOT_FOUND));
    }

    public UserAchievement getByUserAndAchievementId(final AchievementEvent event) {
        return userAchievementRepository
            .findByUserAndAchievementId(event.getUserId(), event.getAchievementId())
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
    }

}
