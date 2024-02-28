package io.oeid.mogakgo.domain.review.application.dto.req;

import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateReq {
    private Long senderId;
    private Long receiverId;
    private Long projectId;
    private ReviewRating rating;
}
