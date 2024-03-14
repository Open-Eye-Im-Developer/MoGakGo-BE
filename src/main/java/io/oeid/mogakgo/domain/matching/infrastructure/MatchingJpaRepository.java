package io.oeid.mogakgo.domain.matching.infrastructure;

import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingJpaRepository extends JpaRepository<Matching, Long>,
    MatchingRepositoryCustom {

    @Query("select m from Matching m join Project p on m.project.id = p.id "
        + "where (m.sender.id = :userId or p.creator.id = :userId) and m.matchingStatus = 'PROGRESS'")
    List<Matching> findProgressOneByUserId(Long userId, Pageable pageable);

    @Query(value = """
        select sum(if(p.region, 1, 0))
        from matching_tb as m
        inner join project_tb as p on m.project_id = p.id
        where m.sender_id = 11 or p.creator_id = 11
        and m.matching_status = 'FINISHED' or m.matching_status = 'PROGRESS';
    """, nativeQuery = true)
    Integer findRegionCountByMatching(Long userId);
}
