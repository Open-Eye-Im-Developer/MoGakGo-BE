package io.oeid.mogakgo.domain.review.infrastructure;

import io.oeid.mogakgo.domain.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

}