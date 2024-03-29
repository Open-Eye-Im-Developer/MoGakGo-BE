package io.oeid.mogakgo.domain.project_join_req.infrastructure;

import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProjectJoinRequestJpaRepository extends JpaRepository<ProjectJoinRequest, Long>,
    ProjectJoinRequestRepositoryCustom {

    @EntityGraph(attributePaths = {"project"})
    @Query("select pjr from ProjectJoinRequest pjr where pjr.id = :id")
    Optional<ProjectJoinRequest> findByIdWithProject(Long id);

    @Query("select pjr from ProjectJoinRequest pjr where pjr.sender.id = :senderId and pjr.requestStatus = 'PENDING'")
    Optional<ProjectJoinRequest> findPendingBySenderId(Long senderId);

    @Query("select pjr from ProjectJoinRequest pjr where pjr.sender.id = :userId and pjr.requestStatus = 'PENDING'")
    Optional<ProjectJoinRequest> findAnotherRequestAlreadyExist(Long userId);

    @Modifying
    @Query("update ProjectJoinRequest pjr set pjr.requestStatus = 'REJECTED' "
        + "where pjr.project.id = :projectId and pjr.id != :acceptedRequestId and pjr.requestStatus = 'PENDING'")
    int rejectNoAcceptedByProjectId(Long projectId, Long acceptedRequestId);

    @Modifying
    @Query("update ProjectJoinRequest pjr set pjr.requestStatus = 'REJECTED' where pjr.project.id = :projectId and pjr.requestStatus = 'PENDING'")
    int rejectAllByProjectId(Long projectId);

    @Query("""
        select pjr from ProjectJoinRequest pjr where pjr.project.id = :projectId and pjr.requestStatus = 'REJECTED'
    """)
    List<ProjectJoinRequest> findRejectedRequestByProjectId(Long projectId);
}
