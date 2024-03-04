package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.domain.chat.application.ChatWebSocketService;
import io.oeid.mogakgo.domain.chat.application.dto.req.ChatReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.ChatApiReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatWebSocketService chatWebSocketService;
    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/topic/chatroom/{chatRoomId}")
    public ChatDataRes sendChatData(@DestinationVariable("chatRoomId") String chatRoomId, ChatApiReq request) {
        return chatWebSocketService.handleChatMessage(request.getUserId(), chatRoomId, ChatReq.from(request));
    }
}
