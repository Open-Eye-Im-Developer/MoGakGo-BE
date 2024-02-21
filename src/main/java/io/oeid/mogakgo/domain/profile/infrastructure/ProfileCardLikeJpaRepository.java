package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileCardLikeJpaRepository extends JpaRepository<ProfileCardLike, Long>,
    ProfileCardLikeRepositoryCustom {

}
