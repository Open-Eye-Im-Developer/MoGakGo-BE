package io.oeid.mogakgo.domain.chat.application;


import static io.oeid.mogakgo.exception.code.ErrorCode500.CHAT_WEB_SOCKET_ERROR;

import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRoomJpaRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomSessionRepository;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatWebSocketService {

    private final ChatRoomRoomJpaRepository chatRoomJpaRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomSessionRepository chatRoomSessionRepository;

    @Transactional(readOnly = true)
    public ChatRoom findChatRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId).orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        if(chatRoom.getStatus().equals(ChatStatus.CLOSED)){
            throw new ChatException(ErrorCode400.CHAT_ROOM_CLOSED);
        }
        return chatRoom;
    }

    public void saveChatMessage(ChatMessage chatMessage, String roomId) {
        chatRepository.save(chatMessage, roomId);
    }

    public void closeChatRoom(String roomId) {
        ChatRoom chatRoom = chatRoomJpaRepository.findById(roomId).orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        chatRoomSessionRepository.removeRoom(chatRoom.getId());
        chatRoom.closeChat();
    }

    public void addSessionToRoom(String roomId, WebSocketSession session) {
        chatRoomSessionRepository.addSession(roomId, session);
    }

    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        chatRoomSessionRepository.removeSession(roomId, session);
    }

    public void sendMessageToEachSocket(String roomId, TextMessage textMessage){
        chatRoomSessionRepository.getSession(roomId).forEach(session -> {
            try {
                session.sendMessage(textMessage);
            } catch (Exception e) {
                log.error("sendMessageToEachSocket: {}", e.getMessage());
                throw new ChatException(CHAT_WEB_SOCKET_ERROR);
            }
        });
    }
}
