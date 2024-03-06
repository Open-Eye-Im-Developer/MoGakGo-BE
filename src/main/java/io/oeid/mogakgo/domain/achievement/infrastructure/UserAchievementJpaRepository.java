package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.entity.UserAchievement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAchievementJpaRepository extends JpaRepository<UserAchievement, Long>, UserAchievementRepositoryCustom {

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.id = :achievementId")
    Optional<UserAchievement> findByUserAndAchievementId(Long userId, Long achievementId);
}
