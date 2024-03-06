package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.application.dto.res.AchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import java.util.List;

public interface UserAchievementRepositoryCustom {

    List<UserAchievementInfoRes> getAchievedOrInProcessUserAchievementInfo(Long userId);
    List<AchievementInfoRes> getNonAchievedAchievementInfo(Long userId);
}
