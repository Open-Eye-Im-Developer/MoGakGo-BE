package io.oeid.mogakgo.domain.chat.application;


import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRepository;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatWebSocketService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public ChatRoom findChatRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        if(chatRoom.getStatus().equals(ChatStatus.CLOSED)){
            throw new ChatException(ErrorCode400.CHAT_ROOM_CLOSED);
        }
        return chatRoom;
    }

    public void manageChatCollections(String roomId) {
        chatRepository.createCollection(roomId);
    }

    public void saveChatMessage(ChatMessage chatMessage, String roomId) {
        chatRepository.save(chatMessage, roomId);
    }


}
