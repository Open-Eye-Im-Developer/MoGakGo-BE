package io.oeid.mogakgo.domain.chat.application.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomCreateReq {
    private Long projectId;
    private Long senderId;
}
