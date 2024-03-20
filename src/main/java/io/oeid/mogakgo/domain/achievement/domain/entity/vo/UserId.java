package io.oeid.mogakgo.domain.achievement.domain.entity.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserId {

    private final Long userId;

    @Builder
    @JsonCreator
    private UserId(Long userId) {
        this.userId = userId;
    }

}
