package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJoinRequestJpaRepository extends JpaRepository<ProjectJoinRequest, Long>,
    ProjectJoinRequestRepositoryCustom {

    boolean existsByProjectId(Long projectId);

}
