package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.chat.application.dto.req.ChatRoomCreateReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomCreateRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRoomJpaRepository;
import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.exception.ProjectException;
import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.util.List;
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
    private final ChatRepository chatRepository;
    private final ProjectJpaRepository projectRepository;

    // 채팅방 리스트 조회
    // TODO 마지막 채팅 기록 가져오기 구현
    public List<ChatRoomPublicRes> findAllChatRoomByUserId(Long userId) {
        userCommonService.getUserById(userId);
        return chatRoomRepository.findAllChatRoomByUserId(userId);
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomCreateRes createChatRoom(Long creatorId, ChatRoomCreateReq request) {
        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new MatchingException(ErrorCode404.PROJECT_NOT_FOUND));
        User creator = userCommonService.getUserById(creatorId);
        User sender = userCommonService.getUserById(request.getSenderId());
        ChatRoom chatRoom = chatRoomRepository.save(
            ChatRoom.builder().project(project).creator(creator).sender(sender).build());
        chatRepository.createCollection(chatRoom.getId());
        return ChatRoomCreateRes.from(chatRoom);
    }

    // 채팅방 조회
    public CursorPaginationResult<ChatDataRes> findAllChatInChatRoom(Long userId, String chatRoomId, CursorPaginationInfoReq pageable) {
        var user = userCommonService.getUserById(userId);
        var chatRoom = findChatRoomById(chatRoomId);
        chatRoom.validateContainsUser(user);
        return null;
    }

    public ChatRoomDataRes findChatRoomDetailData(Long userId, String chatRoomId) {
        var user = userCommonService.getUserById(userId);
        var chatRoom = findChatRoomById(chatRoomId);
        chatRoom.validateContainsUser(user);
        var project = projectRepository.findById(chatRoom.getProject().getId())
            .orElseThrow(() -> new ProjectException(ErrorCode404.PROJECT_NOT_FOUND));
        return ChatRoomDataRes.from(project.getMeetingInfo());
    }

    private ChatRoom findChatRoomById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new MatchingException(ErrorCode404.CHAT_ROOM_NOT_FOUND));
    }
}
