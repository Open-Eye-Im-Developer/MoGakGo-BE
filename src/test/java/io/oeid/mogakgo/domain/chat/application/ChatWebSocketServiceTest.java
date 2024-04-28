package io.oeid.mogakgo.domain.chat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.oeid.mogakgo.domain.chat.entity.document.ChatMessage;
import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomDocumentRepository;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("서비스 테스트 - ChatWebSocketService")
class ChatWebSocketServiceTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String MEET_DETAIL = "meetDetail";
    private static final Double MEET_LOCATION_LATITUDE = 37.579131361776746;
    private static final Double MEET_LOCATION_LONGITUDE = 126.97638456163786;
    private static final Long PROJECT_ID = 1L;
    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;
    private static final String URL_MOCK = "";

    @InjectMocks
    private ChatWebSocketService chatWebSocketService;
    @Mock
    private ChatIdSequenceGeneratorService sequenceGeneratorService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatRoomDocumentRepository chatroomRepository;

    private UUID roomId;
    private ChatRoomDetail chatRoomDetail;
    private ChatRoom chatroom;


    @BeforeEach
    void init() {
        roomId = UUID.randomUUID();
        var user1 = spy(User.of(1L, "user1", URL_MOCK, URL_MOCK, URL_MOCK));
        var user2 = spy(User.of(2L, "user2", URL_MOCK, URL_MOCK, URL_MOCK));
        doReturn(USER1_ID).when(user1).getId();
        doReturn(USER2_ID).when(user2).getId();
        var project = mock(Project.class);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(project.getMeetingInfo()).thenReturn(
            MeetingInfo.of(CURRENT_TIME, CURRENT_TIME.plusHours(1),
                new GeometryFactory().createPoint(
                    new Coordinate(MEET_LOCATION_LATITUDE, MEET_LOCATION_LONGITUDE)), MEET_DETAIL));
        chatRoomDetail = ChatRoomDetail.from(project);
        chatroom = ChatRoom.of(1L, roomId, chatRoomDetail);
        chatroom.addParticipant(user1);
        chatroom.addParticipant(user2);
    }

    @Test
    void 채팅_메시지_핸들링_성공() {
        // Arrange
        var expectedCursorId = 1L;
        var expectedMessage = "hello";
        when(chatroomRepository.findByRoomIdAndUserId(any(UUID.class), any(Long.class))).thenReturn(
            Optional.of(chatroom));
        when(sequenceGeneratorService.generateSequence(any(String.class))).thenReturn(
            expectedCursorId);
        when(chatRepository.save(any(ChatMessage.class), any(String.class))).thenAnswer(
            invocation -> invocation.getArgument(0));
        // Act
        var actualResult = chatWebSocketService.handleChatMessage(roomId, USER1_ID,
            expectedMessage);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("receiverId", USER2_ID)
            .hasFieldOrPropertyWithValue("id", expectedCursorId)
            .hasFieldOrPropertyWithValue("senderId", USER1_ID)
            .hasFieldOrPropertyWithValue("senderUserName", "user1")
            .hasFieldOrPropertyWithValue("message", expectedMessage)
            .hasFieldOrProperty("createdAt").isNotNull();
    }

    @Test
    void 채팅_메시지_핸들링_실패_채팅방이_존재하지_않음() {
        // Arrange
        var expectedRoomId = UUID.randomUUID();
        when(chatroomRepository.findByRoomIdAndUserId(any(UUID.class), any(Long.class))).thenReturn(
            Optional.empty());
        // Act & Assert
        assertThatThrownBy(
            () -> chatWebSocketService.handleChatMessage(expectedRoomId, USER1_ID, "hello"))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅_메시지_핸들링_실패_이미_종료된_채팅방() {
        // Arrange
        when(chatroomRepository.findByRoomIdAndUserId(any(UUID.class), any(Long.class))).thenReturn(
            Optional.of(chatroom));
        chatroom.closeChatRoom();
        // Act & Assert
        assertThatThrownBy(
            () -> chatWebSocketService.handleChatMessage(roomId, USER1_ID, "hello"))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode400.CHAT_ROOM_CLOSED.getMessage());
    }
}