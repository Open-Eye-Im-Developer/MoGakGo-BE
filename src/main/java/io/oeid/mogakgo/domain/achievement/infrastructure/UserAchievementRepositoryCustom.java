package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.presentation.dto.res.UserAchievementDetailInfoRes;
import java.util.List;

public interface UserAchievementRepositoryCustom {

    List<UserAchievementDetailInfoRes> getAchievementInfoAboutUser(Long userId);
    Long getAvailableAchievementWithNull(Long userId, ActivityType activityType);
    Long findMinAchievementIdByActivityType(ActivityType activityType);
    Long getAvailableAchievementWithoutNull(Long userId, ActivityType activityType);
    Integer getAccumulatedProgressCountByActivity(Long userId, ActivityType activityType);
}
