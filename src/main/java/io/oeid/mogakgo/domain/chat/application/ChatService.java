package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.ChatUser;
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
    // TODO 마지막 채팅 기록 가져오기 구현
    // TODO: [2024-03-04] 페이지네이션 적용하기
    // TODO: [2024-03-04] QUERYDSL 최적화 필요 -> ChatUser 추가됨에 따라
    public CursorPaginationResult<ChatRoomPublicRes> findAllChatRoomByUserId(Long userId, CursorPaginationInfoReq pageable) {
        findUserById(userId);
        return chatRoomRepository.findAllChatRoomByUserId(userId, pageable);
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
        findChatUser(chatRoomId, userId);
        return chatRepository.findAllByCollection(chatRoomId, pageable);
    }

    // TODO: QUERYDSL로 JOIN해서 한번에 처리하기 -> ChatUser -> ChatRoom <- Project
    // TODO: 채팅방 상대 정보 조회도 포함해야합니다!
    public ChatRoomDataRes findChatRoomDetailData(Long userId, String chatRoomId) {
        var chatUser = findChatUser(chatRoomId, userId);
//        var project = projectRepository.findById(chatRoom.getProject().getId())
//            .orElseThrow(() -> new ProjectException(ErrorCode404.PROJECT_NOT_FOUND));
//        return ChatRoomDataRes.from(project.getMeetingInfo());
        return null;
    }

    private ChatRoom findChatRoomById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new MatchingException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
    }

    private User findUserById(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private ChatUser findChatUser(String chatRoomIdStr, Long userId) {
        UUID chatRoomId = UUID.fromString(chatRoomIdStr);
        return chatUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
            .orElseThrow(() -> new MatchingException(ErrorCode404.CHAT_USER_NOT_FOUND));
    }

}
