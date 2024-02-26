package io.oeid.mogakgo.domain.chat.application.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "채팅방 유저 정보")
@Getter
@AllArgsConstructor
public class ChatUserInfo {
    @Schema(description = "유저 ID")
    private Long userId;
    @Schema(description = "유저 이름")
    private String username;
    @Schema(description = "유저 프로필 이미지 URL")
    private String avatarUrl;
}
