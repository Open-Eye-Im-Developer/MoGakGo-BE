package io.oeid.mogakgo.domain.user.application.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저의 잔디 점수")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserJandiRateRes {

    @Schema(description = "유저 아이디")
    private Long userId;
    @Schema(description = "유저의 잔디 점수")
    private Double jandiRate;

    public static UserJandiRateRes of(Long userId, Double jandiRate) {
        return new UserJandiRateRes(userId, jandiRate);
    }
}
