package io.oeid.mogakgo.domain.chat.application;


import io.oeid.mogakgo.domain.chat.application.dto.req.ChatReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRoomJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatWebSocketService {

    private final ChatRoomRoomJpaRepository chatRoomJpaRepository;
    private final ChatRepository chatRepository;
    private final ChatIdSequenceGeneratorService sequenceGeneratorService;
    private final UserCommonService userCommonService;

    public ChatDataRes handleChatMessage(Long userId, String roomId, ChatReq request) {
        User user = userCommonService.getUserById(userId);
        verifyChatRoomByRoomIdAndUser(roomId, user);
        ChatMessage chatMessage = chatRepository.save(
            ChatMessage.builder().id(sequenceGeneratorService.generateSequence(roomId))
                .senderId(user.getId())
                .messageType(request.getMessageType())
                .message(request.getMessage())
                .build(), roomId);
        return ChatDataRes.from(chatMessage);
    }

    private void verifyChatRoomByRoomIdAndUser(String roomId, User user) {
        ChatRoom chatRoom = chatRoomJpaRepository.findByIdAndUser(roomId, user)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getStatus().equals(ChatStatus.CLOSED)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_CLOSED);
        }
    }
}
