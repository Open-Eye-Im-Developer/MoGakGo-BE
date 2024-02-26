package io.oeid.mogakgo.domain.matching.infrastructure;

import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchingJpaRepository extends JpaRepository<Matching, Long>,
    MatchingRepositoryCustom {

    @Query("select m from Matching m join Project p on m.project.id = p.id "
        + "where (m.sender.id = :userId or p.creator.id = :userId) and m.matchingStatus = 'PROGRESS'")
    List<Matching> findProgressOneByUserId(Long userId, Pageable pageable);

}
