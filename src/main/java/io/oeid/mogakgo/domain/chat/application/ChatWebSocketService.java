package io.oeid.mogakgo.domain.chat.application;


import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomDocumentRepository;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatWebSocketService {

    private final ChatIdSequenceGeneratorService sequenceGeneratorService;
    private final ChatRepository chatRepository;
    private final ChatRoomDocumentRepository chatroomRepository;

    public ChatDataRes handleChatMessage(UUID roomId, Long userId, String message) {
        var chatRoom = chatroomRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getChatStatus().equals(ChatStatus.CLOSED)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_CLOSED);
        }
        var chatMessage = chatRepository.save(
            ChatMessage.builder().id(sequenceGeneratorService.generateSequence(roomId.toString()))
                .senderId(userId)
                .message(message)
                .build(), roomId.toString());
        chatRoom.updateLastMessage(chatMessage);
        chatroomRepository.save(chatRoom);
        return ChatDataRes.of(chatRoom, chatMessage);
    }

}
