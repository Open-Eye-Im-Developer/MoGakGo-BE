package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ChatSwagger;
import io.oeid.mogakgo.domain.chat.application.ChatService;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatRoomIdApiRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController implements ChatSwagger {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<CursorPaginationResult<ChatRoomPublicRes>> getChatRoomList(
        @UserId Long userId, CursorPaginationInfoReq pageable) {
        return ResponseEntity.ok(chatService.findAllChatRoomByUserId(userId, pageable));
    }

    @GetMapping("/detail/{chatRoomId}")
    public ResponseEntity<ChatRoomDataRes> getChatRoomDetailData(@UserId Long userId,
        @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.findChatRoomDetailData(userId, chatRoomId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<CursorPaginationResult<ChatDataApiRes>> getChatData(
        @PathVariable String chatRoomId,
        @UserId Long userId, @Valid @ModelAttribute CursorPaginationInfoReq pageable) {
        return ResponseEntity.ok(chatService.findAllChatInChatRoom(userId, chatRoomId, pageable));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ChatRoomIdApiRes> getChatRoomByProjectId(@UserId Long userId,
        @PathVariable Long projectId) {
        var chatRoomId = chatService.findChatRoomIdByProjectId(userId, projectId);
        return ResponseEntity.ok(ChatRoomIdApiRes.from(chatRoomId));
    }

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(
        @UserId Long userId, @PathVariable String chatRoomId
    ) {
        chatService.leaveChatroom(userId, chatRoomId);
        return ResponseEntity.noContent().build();
    }
}
