package io.oeid.mogakgo.domain.user.application.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserJandiRateRes {

    private Long userId;
    private Double jandiRate;

    public static UserJandiRateRes of(Long userId, Double jandiRate) {
        return new UserJandiRateRes(userId, jandiRate);
    }
}
