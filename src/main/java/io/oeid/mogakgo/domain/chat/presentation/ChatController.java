package io.oeid.mogakgo.domain.chat.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.domain.chat.application.ChatService;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomPublicRes>> getChatRoomList(@UserId Long userId) {
        return ResponseEntity.ok(chatService.findAllChatRoomByUserId(userId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDataRes> getChatRoomDetailData(@UserId Long userId,
        @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.findAllChatInChatRoom(userId, chatRoomId));
    }
}
