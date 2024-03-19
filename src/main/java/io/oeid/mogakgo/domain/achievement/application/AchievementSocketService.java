package io.oeid.mogakgo.domain.achievement.application;

import static io.oeid.mogakgo.exception.code.ErrorCode500.ACHIEVEMENT_WEB_SOCKET_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.domain.achievement.domain.entity.AchievementMessage;
import io.oeid.mogakgo.domain.achievement.exception.AchievementException;
import io.oeid.mogakgo.domain.achievement.infrastructure.session.AchievementSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementSocketService {

    private final ObjectMapper objectMapper;
    private final AchievementSessionRepository achievementSessionRepository;

    public void addSession(Long userId, WebSocketSession session) {
        achievementSessionRepository.addSession(userId, session);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        achievementSessionRepository.removeSession(userId, session);
    }

    @Async
    public void sendMessageAboutAchievmentCompletion(Long userId, AchievementMessage message) {

        WebSocketSession session = achievementSessionRepository.getSession(userId);

        if (session != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                TextMessage textMessage = new TextMessage(jsonMessage);
                session.sendMessage(textMessage);
            } catch (JsonProcessingException e) {
                log.error("failed to convert Json message from object: {}", e.getMessage());
            } catch (Exception e) {
                log.error("sendMessageToSocket: {}", e.getMessage());
                throw new AchievementException(ACHIEVEMENT_WEB_SOCKET_ERROR);
            }
        }
    }
}
