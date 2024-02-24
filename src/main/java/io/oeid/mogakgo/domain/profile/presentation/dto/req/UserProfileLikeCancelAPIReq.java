package io.oeid.mogakgo.domain.profile.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "관심 있는 프로필 카드에 '찔러보기' 요청 취소 요청 DTO")
@Getter
public class UserProfileLikeCancelAPIReq {

    @Schema(description = "요청 취소하는 사용자 ID", example = "2", implementation = Long.class)
    @NotNull
    private final Long senderId;

    @Schema(description = "요청을 취소할 프로필 카드의 사용자 ID", example = "3", implementation = Long.class)
    @NotNull
    private final Long receiverId;

    @Builder
    private UserProfileLikeCancelAPIReq(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public static UserProfileLikeCancelAPIReq from(Long senderId, Long receiverId) {
        return UserProfileLikeCancelAPIReq.builder()
            .senderId(senderId)
            .receiverId(receiverId)
            .build();
    }

}
