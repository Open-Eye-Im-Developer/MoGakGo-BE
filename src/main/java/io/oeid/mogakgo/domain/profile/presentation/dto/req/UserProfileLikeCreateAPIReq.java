package io.oeid.mogakgo.domain.profile.presentation.dto.req;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "관심 있는 프로필 카드에 대한 찔러보기 요청 생성 DTO")
@Getter
public class UserProfileLikeCreateAPIReq {

    @Schema(description = "찔러보기 요청을 보내는 사용자 ID")
    @NotNull
    private final Long senderId;

    @Schema(description = "찔러보기 요청을 받는 사용자 ID")
    @NotNull
    private final Long receiverId;

    private UserProfileLikeCreateAPIReq(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public ProfileCardLike toEntity(User sender, User receiver) {
        return ProfileCardLike.of(sender, receiver);
    }
}
