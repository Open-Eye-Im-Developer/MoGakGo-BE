package io.oeid.mogakgo.domain.profile.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자가 받은 '찔러보기' 요청 응답 DTO")
@Getter
public class UserProfileLikeAPIRes {

    @Schema(description = "'찔러보기' 요청을 보내는 사용자 ID")
    @NotNull
    private final Long userId;

    @Schema(description = "사용자가 받은 '찔러보기' 요청 수")
    @NotNull
    private final Long likeCount;

    private UserProfileLikeAPIRes(Long userId, Long likeCount) {
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public static UserProfileLikeAPIRes from(Long userId, Long likeCount) {
        return new UserProfileLikeAPIRes(userId, likeCount);
    }

}
