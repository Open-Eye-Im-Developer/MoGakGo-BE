package io.oeid.mogakgo.domain.profile.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;

@Schema(description = "사용자가 보낸 찔러보기 요청 조회 응답 DTO")
@Getter
public class UserProfileLikeInfoAPIRes {

    @Schema(description = "사용자가 찔러보기 요청을 보낸 사용자 ID")
    private final Long id;

    @Schema(description = "사용자가 찔러보기 요청을 보낸 사용자 이름")
    private final String username;

    @Schema(description = "사용자가 찔러보기 요청을 보낸 사용자 아바타 URL")
    private final String avatarUrl;

    @Schema(description = "찔러보기 요청을 보낸 시간")
    @NotNull
    private final LocalDateTime createdAt;

    public UserProfileLikeInfoAPIRes(Long id, String username, String avatarUrl,
        LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public static UserProfileLikeInfoAPIRes from(Long id, String username, String avatarUrl,
        LocalDateTime createdAt
    ) {
        return new UserProfileLikeInfoAPIRes(id, username, avatarUrl, createdAt);
    }
}
