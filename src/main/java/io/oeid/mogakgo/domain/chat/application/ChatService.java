package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomCreateRes;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
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
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatRepository chatRepository;

    public List<ChatRoom> findAllChatRoomByUserId(Long userId) {
        User user = userCommonService.getUserById(userId);
        return chatRoomJpaRepository.findAllByUserId(user.getId());
    }

    @Transactional
    public ChatRoomCreateRes createChatRoom(Long creatorId, Long senderId, String name) {
        User creator = userCommonService.getUserById(creatorId);
        User sender = userCommonService.getUserById(senderId);
        ChatRoom chatRoom = chatRoomJpaRepository.save(
            ChatRoom.builder().creator(creator).sender(sender).name(name).build());
        chatRepository.createCollection(chatRoom.getId());
        return ChatRoomCreateRes.from(chatRoom);
    }
}
