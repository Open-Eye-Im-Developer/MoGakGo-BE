package io.oeid.mogakgo.domain.chat.entity.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("도큐먼트 테스트: ChatMessage")
class ChatMessageTest {

    @Test
    void 채팅_메시지_생성() {
        // Arrange
        long id = 1L;
        long senderId = 1L;
        String message = "안녕하세요";
        // Act
        var actualResult = ChatMessage.builder()
            .id(id)
            .senderId(senderId)
            .message(message)
            .build();
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("id", id)
            .hasFieldOrPropertyWithValue("senderId", senderId)
            .hasFieldOrPropertyWithValue("message", message)
            .hasFieldOrProperty("createdAt").isNotNull();
    }

}