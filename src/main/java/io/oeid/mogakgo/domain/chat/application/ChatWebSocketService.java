package io.oeid.mogakgo.domain.chat.application;


import io.oeid.mogakgo.domain.chat.application.dto.req.ChatReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatUserJpaRepository;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
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

    private final ChatUserJpaRepository chatUserRepository;
    private final ChatRepository chatRepository;
    private final ChatIdSequenceGeneratorService sequenceGeneratorService;
    private final UserJpaRepository userRepository;

    public ChatDataRes handleChatMessage(Long userId, String roomId, ChatReq request) {
        log.info("handleChatMessage userId: {}, roomId: {}", userId, roomId);
        verifyChatRoomByRoomIdAndUser(roomId, userId);
        var receiver = chatUserRepository.findReceiverByRoomIdAndUserId(UUID.fromString(roomId),
                userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
        var sender = userRepository.findById(userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
        ChatMessage chatMessage = chatRepository.save(
            ChatMessage.builder().id(sequenceGeneratorService.generateSequence(roomId))
                .senderId(userId)
                .messageType(request.getMessageType())
                .message(request.getMessage())
                .build(), roomId);
        return ChatDataRes.of(receiver.getUser().getId(), sender.getUsername(), chatMessage);
    }

    private void verifyChatRoomByRoomIdAndUser(String roomId, Long userId) {
        log.info("verifyChatRoomByRoomIdAndUser - roomId: {}, UserId: {}", roomId, userId);
        ChatRoom chatRoom = chatUserRepository.findByChatRoomIdAndUserId(UUID.fromString(roomId),
                userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND)).getChatRoom();
        if (chatRoom.getStatus().equals(ChatStatus.CLOSED)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_CLOSED);
        }
    }
}
