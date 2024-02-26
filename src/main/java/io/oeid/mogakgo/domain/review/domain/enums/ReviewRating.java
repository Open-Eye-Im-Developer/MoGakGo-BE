package io.oeid.mogakgo.domain.review.domain.enums;

import lombok.Getter;

@Getter
public enum ReviewRating {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    ReviewRating(int value) {
        this.value = value;
    }
}
