package io.oeid.mogakgo.domain.review.domain.enums;

import io.oeid.mogakgo.domain.review.exception.ReviewException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import lombok.Getter;

@Getter
public enum ReviewRating {
    ONE(-2),
    TWO(-1),
    THREE(1),
    FOUR(2),
    FIVE(3);

    private final int value;

    ReviewRating(int value) {
        this.value = value;
    }

    public static ReviewRating from(int rating){
        for(ReviewRating reviewRating : ReviewRating.values()){
            if(reviewRating.value == rating){
                return reviewRating;
            }
        }
        throw new ReviewException(ErrorCode400.REVIEW_RATING_INVALID);
    }
}
