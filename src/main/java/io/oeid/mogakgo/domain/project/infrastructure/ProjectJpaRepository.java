package io.oeid.mogakgo.domain.project.infrastructure;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p where p.id = :id and p.deletedAt is null")
    @Override
    Optional<Project> findById(Long id);

    @Query("select p from Project p where p.id = :id")
    Optional<Project> findByIdWithDeleted(Long id);
}
