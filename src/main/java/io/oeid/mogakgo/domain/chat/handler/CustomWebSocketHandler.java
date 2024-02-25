package io.oeid.mogakgo.domain.chat.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode500.CHAT_WEB_SOCKET_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.domain.chat.application.ChatWebSocketService;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatWebSocketService chatWebSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
        throws Exception {
        TextMessage textMessage = (TextMessage) message;
        String payload = textMessage.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        ChatRoom chatRoom = chatWebSocketService.findChatRoomById(chatMessage.getRoomId());
        switch (chatMessage.getMessageType()) {
            case ENTER -> chatWebSocketService.addSessionToRoom(chatRoom.getId(), session);
            case QUIT -> {
                chatWebSocketService.removeSessionFromRoom(chatRoom.getId(), session);
                chatWebSocketService.closeChatRoom(chatRoom.getId());
            }
            default -> chatWebSocketService.sendMessageToEachSocket(chatRoom.getId(), textMessage);
        }
        chatWebSocketService.saveChatMessage(chatMessage, chatRoom.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {
        log.warn("handleTransportError: {}", exception.getMessage());
        throw new ChatException(CHAT_WEB_SOCKET_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {
        log.info("afterConnectionClosed: {}, {}", session.getId(), closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
