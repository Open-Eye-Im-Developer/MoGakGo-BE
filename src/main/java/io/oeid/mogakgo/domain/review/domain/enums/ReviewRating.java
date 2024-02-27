package io.oeid.mogakgo.domain.review.domain.enums;

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
}
