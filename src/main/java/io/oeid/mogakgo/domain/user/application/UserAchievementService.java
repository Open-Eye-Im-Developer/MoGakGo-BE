package io.oeid.mogakgo.domain.user.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.NON_ACHIEVED_USER_ACHIEVEMENT;
import static io.oeid.mogakgo.exception.code.ErrorCode403.USER_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.ACHIEVEMENT_NOT_FOUND;

import io.oeid.mogakgo.domain.achievement.application.AchievementProgressService;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.exception.UserAchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserAchievementUpdateApiRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAchievementService {

    private final UserCommonService userCommonService;
    private final AchievementJpaRepository achievementRepository;
    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementProgressService achievementProgressService;

    @Transactional
    public UserAchievementInfoRes updateAchievement(Long userId, UserAchievementUpdateApiRequest request) {
        User user = userCommonService.getUserById(userId);
        // 토큰 값과 업데이트하려는 사용자 ID의 일치 여부 검증
        validateUserId(user, userId);
        // 해당 업적의 존재 여부 검증
        validateAchievement(request.getAchievementId());
        UserAchievement userAchievement = userAchievementRepository
            .findByUserAndAchievementId(userId, request.getAchievementId())
            .orElseThrow(() -> new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT));
        // 변경하려는 업적의 달성 여부 검증
        validateAvailableUpdateAchievement(userAchievement);
        user.updateAchievement(userAchievement.getAchievement());
        return UserAchievementInfoRes.of(
            userAchievement,
            getProgressCountForAchievement(userId, userAchievement.getAchievement())
        );
    }

    private void validateAvailableUpdateAchievement(UserAchievement userAchievement) {
        if (userAchievement.getCompleted().equals(Boolean.FALSE) &&
            isAlreadyAchievedAchievement(
                userAchievement.getAchievement().getId(),
                userAchievement.getAchievement().getActivityType())) {
            throw new UserAchievementException(NON_ACHIEVED_USER_ACHIEVEMENT);
        }
    }

    private boolean isAlreadyAchievedAchievement(Long achievementId, ActivityType activityType) {
        return !achievementId.equals(userAchievementRepository
            .findMinAchievementIdByActivityType(activityType));
    }

    private Integer getProgressCountForAchievement(Long userId, Achievement achievement) {
        if (achievement.getRequirementType().equals(RequirementType.ACCUMULATE)) {
            return achievementProgressService.getAccumulatedProgressCount(userId, achievement.getActivityType());
        }
        return achievementProgressService.getProgressCountMap(userId,
            List.of(achievement.getActivityType())).get(achievement.getActivityType());
    }

    public void validateUserId(User user, Long userId) {
        if (!user.getId().equals(userId)) {
            throw new UserException(USER_FORBIDDEN_OPERATION);
        }
    }

    private void validateAchievement(Long achievementId) {
        achievementRepository.findById(achievementId)
            .ifPresentOrElse(achievement -> {
            }, () -> {
                throw new AchievementException(ACHIEVEMENT_NOT_FOUND);
            });
    }
}
