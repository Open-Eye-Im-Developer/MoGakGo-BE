package io.oeid.mogakgo.domain.chat.application.dto.req;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatMessageType;
import io.oeid.mogakgo.domain.chat.presentation.dto.req.ChatApiReq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatReq {

    private ChatMessageType messageType;
    private String message;

    public static ChatReq from(ChatApiReq request) {
        return new ChatReq(ChatMessageType.valueOf(request.getMessageType()), request.getMessage());
    }
}
