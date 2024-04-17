package io.oeid.mogakgo.domain.chat.application.dto.req;

import io.oeid.mogakgo.domain.chat.presentation.dto.req.ChatApiReq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatReq {

    private String message;

    public static ChatReq from(ChatApiReq request) {
        return new ChatReq(request.getMessage());
    }
}
