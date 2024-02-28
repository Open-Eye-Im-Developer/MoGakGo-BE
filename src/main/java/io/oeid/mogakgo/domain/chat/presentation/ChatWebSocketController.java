package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.domain.chat.application.ChatWebSocketService;
import io.oeid.mogakgo.domain.chat.application.dto.req.ChatReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.ChatApiReq;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatWebSocketService chatWebSocketService;

    @MessageMapping("/app/{chatRoomId}")
    @SendTo("/queue/{chatRoomId}")
    public ChatDataRes sendChatData(@UserId Long userId, @DestinationVariable String chatRoomId,
        ChatApiReq request) {
        return chatWebSocketService.handleChatMessage(userId, chatRoomId, ChatReq.from(request));
    }
}
