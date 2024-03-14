package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import java.util.List;

public interface UserAchievementRepositoryCustom {

    List<UserAchievementInfoRes> getAchievementInfoAboutUser(Long userId);
    Long findAvailableAchievementByActivityType(Long userId, ActivityType activityType);
    Long findMinAchievementIdByActivityType(ActivityType activityType);
    Integer getAccumulatedProgressCountByActivity(Long userId, ActivityType activityType);
}
