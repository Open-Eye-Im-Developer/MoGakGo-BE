package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileCardJpaRepository extends JpaRepository<ProfileCard, Long>, ProfileCardRepositoryCustom {

}
