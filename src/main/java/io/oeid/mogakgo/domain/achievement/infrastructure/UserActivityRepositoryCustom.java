package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import java.util.List;

public interface UserActivityRepositoryCustom {

    List<UserActivity> getHistoryByActivityType(Long userId, ActivityType activityType, Integer limit);
}
