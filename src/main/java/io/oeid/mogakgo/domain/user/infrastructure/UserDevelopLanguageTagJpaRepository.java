package io.oeid.mogakgo.domain.user.infrastructure;

import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDevelopLanguageTagJpaRepository extends
    JpaRepository<UserDevelopLanguageTag, Long> {

}