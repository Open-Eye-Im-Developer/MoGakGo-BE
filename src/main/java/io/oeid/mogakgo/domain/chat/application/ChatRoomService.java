package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomRes;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomDocumentRepository;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomDocumentRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatIdSequenceGeneratorService sequenceGeneratorService;

    @Transactional
    public void createChatRoom(Project project, User creator, User sender) {
        var cursorId = sequenceGeneratorService.generateSequence("chatroom_metadata");
        var chatRoom = ChatRoom.of(cursorId, UUID.randomUUID(), ChatRoomDetail.from(project));
        chatRoom.addParticipant(creator);
        chatRoom.addParticipant(sender);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void leaveChatRoom(UUID roomId, Long userId) {
        var chatRoom = chatRoomRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        chatRoom.leaveChatRoom(userId);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomRes> findChatRoomsByUserId(@NonNull Long userId,
        @NonNull Long cursorId, int pageSize) {
        return chatRoomRepository.findChatRoomsByUserId(userId, cursorId, pageSize).stream()
            .map(ChatRoomRes::from).toList();
    }

    public ChatRoomDataRes findChatRoomDetailData(UUID roomId, Long userId) {
        var chatRoom = chatRoomRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        var userInfo = chatRoom.getParticipants().values().stream()
            .filter(info -> !info.userId().equals(userId))
            .findFirst().orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
        return new ChatRoomDataRes(chatRoom.getChatRoomDetail(), userInfo);
    }

    public UUID findChatRoomIdByProjectId(Long projectId) {
        return chatRoomRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND)).getRoomId();
    }

    public List<ChatDataApiRes> findChatMessagesByRoomId(UUID roomId, Long userId,
        Long cursorId, int pageSize) {
        var chatRoom = chatRoomRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
        return chatRepository.findAllByCollection(chatRoom.getRoomId().toString(), cursorId,
            pageSize).stream().map(ChatDataApiRes::from).toList();
    }


}
