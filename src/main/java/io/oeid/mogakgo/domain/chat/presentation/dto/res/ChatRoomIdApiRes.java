package io.oeid.mogakgo.domain.chat.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅방 ID")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChatRoomIdApiRes {

    @Schema(description = "채팅방 ID")
    private String chatRoomId;

    public static ChatRoomIdApiRes from(String chatRoomId) {
        return new ChatRoomIdApiRes(chatRoomId);
    }
}
