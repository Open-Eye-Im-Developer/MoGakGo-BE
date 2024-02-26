package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import java.util.List;

public interface ChatRoomCustomRepository {
    List<ChatRoomPublicRes> findAllChatRoomByUserId(Long userId);
}
