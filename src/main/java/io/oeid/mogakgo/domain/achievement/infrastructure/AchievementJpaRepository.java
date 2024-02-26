package io.oeid.mogakgo.domain.achievement.infrastructure;

import io.oeid.mogakgo.domain.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementJpaRepository extends JpaRepository<Achievement, Long> {

}