package io.oeid.mogakgo.domain.user.infrastructure;


import io.oeid.mogakgo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Override
    @Query("select u from User u where u.id = :id and u.deletedAt is null")
    Optional<User> findById(Long id);

    @Query("select u from User u where u.githubPk = :githubPk and u.deletedAt is null")
    Optional<User> findByGithubPk(Long githubPk);
}