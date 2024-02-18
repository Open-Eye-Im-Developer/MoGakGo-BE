package io.oeid.mogakgo.domain.user.infrastructure;

import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWantedJobTagJpaRepository extends JpaRepository<UserWantedJobTag, Long> {

}