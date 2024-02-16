package io.oeid.mogakgo.domain.project.infrastructure;

import io.oeid.mogakgo.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

}
