package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import java.util.List;
import java.util.UUID;

public interface ChatRoomCustomRepository {

    ChatRoomDataRes getChatDetailData(Long userId, UUID chatRoomId);

    List<ChatRoomPublicRes> getChatRoomList(Long userId, CursorPaginationInfoReq pageable);
}
