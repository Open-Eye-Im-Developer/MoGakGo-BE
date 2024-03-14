package io.oeid.mogakgo.domain.achievement.application;

import static io.oeid.mogakgo.exception.code.ErrorCode404.ACHIEVEMENT_NOT_FOUND;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementFacadeService {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementJpaRepository achievementRepository;
    private final UserActivityJpaRepository userActivityRepository;

    public boolean validateActivityDuplicate(Long userId, ActivityType activityType) {
        return userActivityRepository.findByActivityTypeAndCreatedAt(userId, activityType)
            .isEmpty();
    }

    public Achievement getById(Long achievementId) {
        return achievementRepository.findById(achievementId)
            .orElseThrow(() -> new AchievementException(ACHIEVEMENT_NOT_FOUND));
    }

    public Boolean getUserAchievementByAchievementId(Long userId, Long achievementId) {
        return userAchievementRepository
            .findByUserAndAchievementId(userId, achievementId).isPresent();
    }

    public Long getMinAchievementByActivityType(ActivityType activityType) {
        return userAchievementRepository.findMinAchievementIdByActivityType(activityType);
    }

    public Long findAvailableAchievement(Long userId, ActivityType activityType) {
        return userAchievementRepository
            .findAvailableAchievementByActivityType(userId, activityType);
    }

    public Integer getAccumulatedProgressCount(Long userId, ActivityType activityType) {
        return userAchievementRepository.getAccumulatedProgressCountByActivity(userId, activityType);
    }
}
