package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRoomJpaRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatUserJpaRepository;
import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final UserCommonService userCommonService;
    private final ChatRoomRoomJpaRepository chatRoomRepository;
    private final ChatUserJpaRepository chatUserRepository;
    private final ChatRepository chatRepository;

    // 채팅방 리스트 조회
    public CursorPaginationResult<ChatRoomPublicRes> findAllChatRoomByUserId(Long userId,
        CursorPaginationInfoReq pageable) {
        log.info("findAllChatRoomByUserId - userId: {}", userId);
        var chatRoomList = chatRoomRepository.getChatRoomList(userId, pageable.getCursorId(),
            pageable.getPageSize());
        var result = chatRoomList.stream().map(
            chatRoom -> {
                var user = chatRoom.getOppositeUser(userId);
                ChatRoomPublicRes res = ChatRoomPublicRes.of(chatRoom, user);
                var chatMessage = chatRepository.findLastChatByCollection(
                    chatRoom.getId().toString());
                chatMessage.ifPresent(
                    message -> res.addLastMessage(message.getMessage(), message.getCreatedAt()));
                return res;
            }
        ).toList();
        return CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize());
    }

    // 채팅방 생성
    @Transactional
    public void createChatRoom(Project project, User creator, User sender) {
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(project));
        chatUserRepository.save(chatRoom.join(creator));
        chatUserRepository.save(chatRoom.join(sender));
        chatRepository.createCollection(chatRoom.getId().toString());
    }

    @Transactional
    public void leaveChatroom(Long userId, String chatRoomId) {
        var user = findUserById(userId);
        var chatRoom = findChatRoomById(chatRoomId);
        chatRoom.leaveUser(user);
        // 채팅방 비활성화
        chatRoom.closeChat();
    }

    // 채팅방 조회
    public CursorPaginationResult<ChatDataRes> findAllChatInChatRoom(Long userId, String chatRoomId,
        CursorPaginationInfoReq pageable) {
        verifyChatUser(chatRoomId, userId);
        return chatRepository.findAllByCollection(chatRoomId, pageable);
    }

    public ChatRoomDataRes findChatRoomDetailData(Long userId, String chatRoomId) {
        log.info("findChatRoomDetailData - userId: {}, chatRoomId: {}", userId, chatRoomId);
        verifyChatUser(chatRoomId, userId);
        return chatRoomRepository.getChatDetailData(userId, UUID.fromString(chatRoomId));
    }

    private ChatRoom findChatRoomById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new MatchingException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
    }

    private User findUserById(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void verifyChatUser(String chatRoomIdStr, Long userId) {
        log.info("verifyChatUser - chatRoomId: {}, userId: {}", chatRoomIdStr, userId);
        UUID chatRoomId = UUID.fromString(chatRoomIdStr);
        chatUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
            .ifPresentOrElse(chatuser -> {
                },
                () -> {
                    throw new MatchingException(ErrorCode404.CHAT_USER_NOT_FOUND);
                });
    }

}
