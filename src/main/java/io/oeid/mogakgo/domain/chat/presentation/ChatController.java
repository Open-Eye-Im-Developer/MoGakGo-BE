package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ChatSwagger;
import io.oeid.mogakgo.domain.chat.application.ChatService;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatDataApiRes;
import io.oeid.mogakgo.domain.chat.presentation.dto.res.ChatRoomIdApiRes;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        @AuthenticationPrincipal Long userId,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable) {
        var result = chatService.findChatRoomsByUserId(userId, pageable.getCursorId(), pageable.getPageSize());
        return ResponseEntity.ok(CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize()));
    }

    @GetMapping("/detail/{chatRoomId}")
    public ResponseEntity<ChatRoomDataRes> getChatRoomDetailData(
        @AuthenticationPrincipal Long userId,
        @PathVariable String chatRoomId) {
        return ResponseEntity.ok(
            chatService.findChatRoomDetailData(UUID.fromString(chatRoomId), userId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<CursorPaginationResult<ChatDataApiRes>> getChatData(
        @PathVariable String chatRoomId, @AuthenticationPrincipal Long userId,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable) {
        var result = chatService.findChatMessagesByRoomId(UUID.fromString(chatRoomId), userId,
            pageable.getCursorId(), pageable.getPageSize());
        return ResponseEntity.ok(CursorPaginationResult.fromDataWithExtraItemForNextCheck(result,
            pageable.getPageSize()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ChatRoomIdApiRes> getChatRoomByProjectId(
        @AuthenticationPrincipal Long userId, @PathVariable Long projectId) {
        var chatRoomId = chatService.findChatRoomIdByProjectId(projectId);
        return ResponseEntity.ok(ChatRoomIdApiRes.from(chatRoomId.toString()));
    }

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(@AuthenticationPrincipal Long userId,
        @PathVariable String chatRoomId) {
        chatService.leaveChatRoom(UUID.fromString(chatRoomId), userId);
        return ResponseEntity.noContent().build();
    }
}
