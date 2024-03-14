package io.oeid.mogakgo.domain.project.infrastructure;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    @Query("select p from Project p where p.id = :id and p.deletedAt is null")
    @Override
    Optional<Project> findById(Long id);

    @Query("select p from Project p where p.id = :id")
    Optional<Project> findByIdWithDeleted(Long id);

    @Query("select p from Project p "
        + "where p.creator.id = :creatorId and p.projectStatus in ('PENDING', 'MATCHED')")
    List<Project> findNotEndProjectOneByCreatorId(Long creatorId, Pageable pageable);

    @Query(value = """
        select sum(if(p.region, 1, 0)) from project_tb as p where p.creator_id = :userId
    """, nativeQuery = true)
    Integer getRegionCountByUserId(Long userId);
}
