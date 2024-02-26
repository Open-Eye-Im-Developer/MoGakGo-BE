package io.oeid.mogakgo.domain.chat.infrastructure;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

@Repository
public class ChatRoomSessionRepository {
    private final Map<String, Set<WebSocketSession>> sessionStorage = new ConcurrentHashMap<>();

    public void addSession(String roomId, WebSocketSession session) {
        sessionStorage.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public Set<WebSocketSession> getSession(String roomId) {
        return sessionStorage.get(roomId);
    }

    public void removeSession(String roomId, WebSocketSession session) {
        sessionStorage.get(roomId).remove(session);
    }

    public void removeRoom(String roomId) {
        sessionStorage.remove(roomId);
    }
}
