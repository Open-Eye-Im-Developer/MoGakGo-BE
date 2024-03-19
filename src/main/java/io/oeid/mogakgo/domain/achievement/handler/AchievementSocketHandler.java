package io.oeid.mogakgo.domain.achievement.handler;

import static io.oeid.mogakgo.exception.code.ErrorCode500.ACHIEVEMENT_WEB_SOCKET_ERROR;

import io.oeid.mogakgo.domain.achievement.application.AchievementSocketService;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementSocketHandler extends TextWebSocketHandler {

    private final AchievementSocketService achievementSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        achievementSocketService.addSession(userId, session);
        log.info("afterConnectionEstablished: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        throw new UnsupportedOperationException();
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
