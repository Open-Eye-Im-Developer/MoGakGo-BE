package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ChatSwagger;
import io.oeid.mogakgo.domain.chat.application.ChatService;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController implements ChatSwagger {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomPublicRes>> getChatRoomList(@UserId Long userId) {
        return ResponseEntity.ok(chatService.findAllChatRoomByUserId(userId));
    }

    @GetMapping("/detail/{chatRoomId}")
    public ResponseEntity<ChatRoomDataRes> getChatRoomDetailData(@UserId Long userId,
        @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.findChatRoomDetailData(userId, chatRoomId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<CursorPaginationResult<ChatDataRes>> getChatData(
        @PathVariable String chatRoomId,
        @UserId Long userId, @Valid @ModelAttribute CursorPaginationInfoReq pageable) {
        return ResponseEntity.ok(chatService.findAllChatInChatRoom(userId, chatRoomId, pageable));
    }

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(
        @UserId Long userId, @PathVariable String chatRoomId
    ) {
        chatService.leaveChatroom(userId, chatRoomId);
        return ResponseEntity.noContent().build();
    }
}
