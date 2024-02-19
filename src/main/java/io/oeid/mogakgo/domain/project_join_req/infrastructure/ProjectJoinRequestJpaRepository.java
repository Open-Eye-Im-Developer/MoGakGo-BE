package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectJoinRequestJpaRepository extends JpaRepository<ProjectJoinRequest, Long>,
    ProjectJoinRequestRepositoryCustom {

    boolean existsByProjectId(Long projectId);

    @Query("select pjr from ProjectJoinRequest pjr where pjr.sender.id = :userId and pjr.project.id = :projectId")
    Optional<ProjectJoinRequest> findAlreadyExists(Long userId, Long projectId);
}
