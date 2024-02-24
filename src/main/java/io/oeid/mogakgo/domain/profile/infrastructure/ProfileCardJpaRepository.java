package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileCardJpaRepository extends JpaRepository<ProfileCard, Long>,
    ProfileCardRepositoryCustom {

    @Query("select pf from ProfileCard pf where pf.user.id = :userId and pf.user.deletedAt is null")
    Optional<ProfileCard> findByUserId(Long userId);
}
