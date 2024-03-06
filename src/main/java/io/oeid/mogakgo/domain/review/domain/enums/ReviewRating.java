package io.oeid.mogakgo.domain.review.domain.enums;

import io.oeid.mogakgo.domain.review.exception.ReviewException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import lombok.Getter;

// TODO: 리뷰 점수 로직 수정
@Getter
public enum ReviewRating {
    ONE(1, -2),
    TWO(2, -1),
    THREE(3, 1),
    FOUR(4, 2),
    FIVE(5, 3);

    private final int value;
    private final int jandiValue;

    ReviewRating(int value, int jandiValue) {
        this.value = value;
        this.jandiValue = jandiValue;
    }

    public static ReviewRating from(int rating) {
        for (ReviewRating reviewRating : ReviewRating.values()) {
            if (reviewRating.value == rating) {
                return reviewRating;
            }
        }
        throw new ReviewException(ErrorCode400.REVIEW_RATING_INVALID);
    }
}
