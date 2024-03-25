package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.domain.chat.application.ChatWebSocketService;
import io.oeid.mogakgo.domain.chat.application.dto.req.ChatReq;
import io.oeid.mogakgo.domain.chat.presentation.dto.req.ChatApiReq;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import io.oeid.mogakgo.domain.notification.application.FCMNotificationService;
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
    private final FCMNotificationService fcmNotificationService;

    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/topic/chatroom/{chatRoomId}")
    public ChatDataApiRes sendChatData(@DestinationVariable("chatRoomId") String chatRoomId,
        ChatApiReq request) {
        log.info("ChatWebSocketController - sendChatData start -> chatRoomId: {}", chatRoomId);
        var response = chatWebSocketService.handleChatMessage(request.getUserId(), chatRoomId,
            ChatReq.from(request));
        fcmNotificationService.sendNotification(response.getReceiverId(),
            response.getSenderUserName(), response.getMessage());
        return response.toApiResponse();
    }
}
