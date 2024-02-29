package io.oeid.mogakgo.domain.review.application.dto.req;

import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import io.oeid.mogakgo.domain.review.presentation.dto.req.ReviewCreateApiReq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewCreateReq {
    private Long senderId;
    private Long receiverId;
    private Long projectId;
    private ReviewRating rating;

    public static ReviewCreateReq from(ReviewCreateApiReq request) {
        return new ReviewCreateReq(request.getSenderId(),
            request.getReceiverId(),
            request.getProjectId(),
            ReviewRating.from(request.getRating()));
    }
}
