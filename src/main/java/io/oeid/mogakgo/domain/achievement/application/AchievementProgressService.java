package io.oeid.mogakgo.domain.achievement.application;

import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserActivityJpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementProgressService {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final UserActivityJpaRepository userActivityRepository;


    public Integer getAccumulatedProgressCount(Long userId, ActivityType activityType) {
        return userAchievementRepository.getAccumulatedProgressCountByActivity(userId, activityType);
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
}
