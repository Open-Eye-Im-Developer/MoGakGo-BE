package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.entity.Achievement;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementJpaRepository extends JpaRepository<Achievement, Long> {

    @Query("select ac from Achievement ac where ac.id = :id")
    Optional<Achievement> findById(Long id);

    @Query("""
        select ac.activityType from Achievement ac join UserAchievement ua
        on ac.id = ua.achievement.id and ua.user.id = :userId
        where ac.requirementType = :requirementType group by ac.activityType
    """)
    List<ActivityType> findActivityTypeInProgress(Long userId, RequirementType requirementType);

}
