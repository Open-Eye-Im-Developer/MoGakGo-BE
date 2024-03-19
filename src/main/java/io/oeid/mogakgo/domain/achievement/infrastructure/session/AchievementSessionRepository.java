package io.oeid.mogakgo.domain.achievement.infrastructure.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

@Repository
public class AchievementSessionRepository {

    private final Map<Long, Set<WebSocketSession>> sessionStorage = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        sessionStorage.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public Set<WebSocketSession> getSession(Long userId) {
        return sessionStorage.get(userId);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        sessionStorage.get(userId).remove(session);
    }
}
