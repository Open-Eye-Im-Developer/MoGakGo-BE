package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.entity.UserActivity;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityJpaRepository extends JpaRepository<UserActivity, Long>,
    UserActivityRepositoryCustom {

    @Query("""
        select uact from UserActivity uact where uact.user.id = :userId
        and uact.activityType = :activityType and uact.deletedAt is null order by uact.createdAt desc
    """)
    List<UserActivity> findByUserIdAndActivityType(Long userId, ActivityType activityType);

    @Query("""
        select uact from UserActivity uact where uact.user.id = :userId and uact.activityType = :activityType
        and FUNCTION('DATE_FORMAT', uact.createdAt, '%Y-%m-%d') = CURRENT_DATE
    """)
    Optional<UserActivity> findByActivityTypeAndCreatedAt(Long userId, ActivityType activityType);

}
