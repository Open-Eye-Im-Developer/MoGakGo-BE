package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.UUID;

public interface ChatRoomCustomRepository {

    ChatRoomPublicRes getChatDetailData(Long userId, UUID chatRoomId);

    List<ChatRoom> getChatRoomList(Long userId, Long cursorId, int pageSize);
}
