package io.oeid.mogakgo.domain.profile.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "관심 있는 프로필 카드에 찔러보기 요청 생성 응답 DTO")
@Getter
public class UserProfileLikeCreateAPIRes {

    @Schema(description = "프로필 카드에 대한 찔러보기 요청 생성 ID")
    @NotNull
    private final Long id;

    private UserProfileLikeCreateAPIRes(Long id) {
        this.id = id;
    }

    public static UserProfileLikeCreateAPIRes of(Long id) {
        return new UserProfileLikeCreateAPIRes(id);
    }
}
