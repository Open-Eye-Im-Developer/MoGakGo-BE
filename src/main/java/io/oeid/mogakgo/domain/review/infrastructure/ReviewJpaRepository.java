package io.oeid.mogakgo.domain.review.infrastructure;

import io.oeid.mogakgo.domain.review.domain.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.sender.id = ?1 and r.receiver.id = ?2 and r.project.id = ?3")
    Optional<Review> findReviewByProjectData(Long id, Long id1, Long id2);
}