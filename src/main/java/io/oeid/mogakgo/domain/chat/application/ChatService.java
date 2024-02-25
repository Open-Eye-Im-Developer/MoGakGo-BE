package io.oeid.mogakgo.domain.chat.application;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomRepository;
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
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> findAllChatRoomByUserId(Long userId) {
        User user = userCommonService.getUserById(userId);
        return chatRoomRepository.findAllByUserId(user.getId());
    }

    @Transactional
    public ChatRoom createChatRoom(Long creatorId, Long senderId, String name) {
        User creator = userCommonService.getUserById(creatorId);
        User sender = userCommonService.getUserById(senderId);
        ChatRoom chatRoom = ChatRoom.builder()
            .creator(creator)
            .sender(sender)
            .name(name)
            .build();
        return chatRoomRepository.save(chatRoom);
    }
}
