package io.oeid.mogakgo.domain.achievement.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode500.ACHIEVEMENT_WEB_SOCKET_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.domain.achievement.application.AchievementSocketService;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AchievementSocketService achievementSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        TextMessage textMessage = (TextMessage) message;
        String payload = textMessage.getPayload();
        Long userId = objectMapper.readValue(payload, Long.class);
        achievementSocketService.validateUser(userId);
        achievementSocketService.addSession(userId, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("handleTransportError: {}", exception.getMessage());
        throw new AchievementException(ACHIEVEMENT_WEB_SOCKET_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
        throws Exception {
        log.info("afterConnectionClosed: {}, {}", session.getId(), status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
