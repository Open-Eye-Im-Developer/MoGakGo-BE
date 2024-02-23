package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfileCardLikeJpaRepository extends JpaRepository<ProfileCardLike, Long>,
    ProfileCardLikeRepositoryCustom {

    @Query("select pcl from ProfileCardLike pcl where pcl.sender.id = :senderId and pcl.receiver.id = :receiverId")
    Optional<ProfileCardLike> findBySenderAndReceiver(Long senderId, Long receiverId);
}
