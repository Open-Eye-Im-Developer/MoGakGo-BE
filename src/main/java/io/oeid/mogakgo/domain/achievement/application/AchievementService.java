package io.oeid.mogakgo.domain.achievement.application;

import static io.oeid.mogakgo.exception.code.ErrorCode403.ACHIEVEMENT_FORBIDDEN_OPERATION;

import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AchievementService {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final AchievementJpaRepository achievementRepository;
    private final UserCommonService userCommonService;
    private final UserActivityJpaRepository userActivityRepository;
    private final AchievementFacadeService achievementFacadeService;

    public List<UserAchievementInfoRes> getUserAchievementInfo(Long userId, Long id) {
        User user = validateToken(userId);
        validateUser(user, id);

        // 사용자의 미달성, Accumulate 타입 업적에 대한 상세 조회
        List<UserAchievementInfoRes> result = getNonAchievedAndAccumulateAchiementInfo(userId);

        // 사용자의 Sequence 타입 업적에 대한 상세 조회
        List<UserAchievementInfoRes> seqResult = getSequenceAchievementInfoAboutUser(userId);

        return Stream
            .concat(result.stream(), seqResult.stream())
            .sorted(Comparator.comparing(UserAchievementInfoRes::getAchievementId)).toList();
    }

    private List<UserAchievementInfoRes> getNonAchievedAndAccumulateAchiementInfo(Long userId) {
        return userAchievementRepository.getAchievementInfoAboutUser(userId);
    }

    private List<UserAchievementInfoRes> getSequenceAchievementInfoAboutUser(Long userId) {

        // 사용자가 진행중이거나 달성중인, Sequence 타입 업적
        List<ActivityType> sequenceList = achievementRepository
            .findActivityTypeInProgress(userId, RequirementType.SEQUENCE);

        // -- 진행중이거나 달성중인 Sequence 타입이 없다면 빈 리스트 반환
        if (sequenceList.isEmpty()) return Collections.emptyList();

        // Sequence 타입 업적에 대해, 사용자가 현재 달성해야 하는 업적 ID
        List<Long> achievementIds = sequenceList.stream().map(
            activityType -> achievementFacadeService.getAvailableAchievementIdWithoutNull(userId, activityType)
        ).toList();

        Map<ActivityType, Integer> map = getProgressCountMap(userId, sequenceList);

        return achievementIds.stream().map(achievementId -> {
            Achievement achievement = achievementFacadeService.getById(achievementId);
            return new UserAchievementInfoRes(
                userId,
                achievementId,
                achievement.getTitle(),
                achievement.getImgUrl(),
                achievement.getDescription(),
                achievement.getRequirementType(),
                achievement.getRequirementValue(),
                map.get(achievement.getActivityType()),
                verifyCompleted(achievement, map)
            );
        }).toList();
    }

    public Map<ActivityType, Integer> getProgressCountMap(Long userId, List<ActivityType> activityList) {
        return activityList.stream().collect(Collectors.toMap(
            activityType -> activityType,
            activityType -> {
                List<UserActivity> history = userActivityRepository
                    .findByUserIdAndActivityType(userId, activityType);
                return !history.isEmpty() ? getSeqProgressCountFromToday(history) : 0;
            }
        ));
    }

    private Integer getSeqProgressCountFromToday(List<UserActivity> activityList) {
        LocalDate today = LocalDate.now();
        if (!equalToLocalDate(today, activityList.get(0).getCreatedAt())) {
            today = today.minusDays(1);
        }
        return validateContinuous(today, 0, activityList, 0);
    }

    private int validateContinuous(LocalDate date, int idx, List<UserActivity> activityList, int count) {
        if (idx == activityList.size() || !equalToLocalDate(date, activityList.get(idx).getCreatedAt())) {
            return count;
        }
        return validateContinuous(date.minusDays(1), idx + 1, activityList, count + 1);
    }

    private boolean equalToLocalDate(LocalDate target, LocalDateTime comparison) {
        return target.equals(comparison.toLocalDate());
    }

    private boolean verifyCompleted(Achievement achievement, Map<ActivityType, Integer> map) {
        return achievement.getRequirementValue().equals(map.get(achievement.getActivityType()))
            ? Boolean.TRUE : Boolean.FALSE;
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateUser(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new AchievementException(ACHIEVEMENT_FORBIDDEN_OPERATION);
        }
    }
}
