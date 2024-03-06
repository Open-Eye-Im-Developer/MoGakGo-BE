package io.oeid.mogakgo.domain.achievement.application;

import static io.oeid.mogakgo.exception.code.ErrorCode403.ACHIEVEMENT_FORBIDDEN_OPERATION;

import io.oeid.mogakgo.domain.achievement.application.dto.res.AchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AchievementService {

    private final UserAchievementJpaRepository userAchievementRepository;
    private final UserCommonService userCommonService;

    public List<UserAchievementInfoRes> getUserAchievementInfo(Long userId, Long id) {
        User user = validateToken(userId);
        validateUser(user, id);

        return userAchievementRepository.getAchievedOrInProcessUserAchievementInfo(userId);
    }

    public List<AchievementInfoRes> getAchievementInfo(Long userId, Long id) {
        User user = validateToken(userId);
        validateUser(user, id);

        return userAchievementRepository.getNonAchievedAchievementInfo(userId);
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
