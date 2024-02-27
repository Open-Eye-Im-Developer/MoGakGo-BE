package io.oeid.mogakgo.domain.review.application.dto.res;

import io.oeid.mogakgo.domain.review.domain.Review;
import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewCreateRes {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long projectId;
    private ReviewRating rating;
    private LocalDateTime createdAt;

    public static ReviewCreateRes from(Review review) {
        return new ReviewCreateRes(
            review.getId(),
            review.getSender().getId(),
            review.getReceiver().getId(),
            review.getProject().getId(),
            review.getRating(),
            review.getCreatedAt()
        );
    }
}
