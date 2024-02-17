package io.oeid.mogakgo.domain.user.infrastructure;


import io.oeid.mogakgo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByGithubPk(Long githubPk);
}