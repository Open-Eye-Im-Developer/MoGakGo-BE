package io.oeid.mogakgo.domain.chat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRepository;
import io.oeid.mogakgo.domain.chat.infrastructure.ChatRoomDocumentRepository;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
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
@DisplayName("Service 테스트: ChatService")
class ChatServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String MEET_DETAIL = "meetDetail";
    private static final Double MEET_LOCATION_LATITUDE = 37.579131361776746;
    private static final Double MEET_LOCATION_LONGITUDE = 126.97638456163786;
    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;

    private static User user1;
    private static User user2;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatRoomDocumentRepository chatRoomRepository;

    @Mock
    private ChatIdSequenceGeneratorService sequenceGeneratorService;

    @InjectMocks
    private ChatService chatService;

    private ChatRoom chatRoom;
    private UUID roomId;

    @BeforeAll
    static void setup() {
        var githubPk = 85854384L;
        var username = "tidavid1";
        var avatarUrl = "https://avatars.githubusercontent.com/u/85854384?v=4";
        var githubUrl = "https://github.com/tidavid1";
        var reposUrl = "https://api.github.com/users/tidavid1/repos";
        user1 = spy(User.of(githubPk, username, avatarUrl, githubUrl, reposUrl));
        user2 = spy(User.of(githubPk, username, avatarUrl, githubUrl, reposUrl));
        doReturn(USER1_ID).when(user1).getId();
        doReturn(USER2_ID).when(user2).getId();
    }


    @BeforeEach
    void init() {
        //doReturn(1L).when(sequenceGeneratorService).generateSequence("chatroom_metadata");
        roomId = UUID.randomUUID();
        var project = mock(Project.class);
        doReturn(PROJECT_ID).when(project).getId();
        doReturn(MeetingInfo.of(CURRENT_TIME, CURRENT_TIME.plusHours(1),
            new GeometryFactory().createPoint(
                new Coordinate(MEET_LOCATION_LATITUDE, MEET_LOCATION_LONGITUDE)),
            MEET_DETAIL)).when(project).getMeetingInfo();
        chatRoom = ChatRoom.of(1L, roomId, ChatRoomDetail.from(project));
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
    }

    @Test
    void 채팅방_생성() {
        // Arrange
        var project = mock(Project.class);
        doReturn(PROJECT_ID).when(project).getId();
        doReturn(MeetingInfo.of(CURRENT_TIME, CURRENT_TIME.plusHours(1),
            new GeometryFactory().createPoint(
                new Coordinate(MEET_LOCATION_LATITUDE, MEET_LOCATION_LONGITUDE)),
            MEET_DETAIL)).when(project).getMeetingInfo();
        // Act & Assert
        assertThatNoException().isThrownBy(() -> chatService.createChatRoom(project, user1, user2));
    }

    @Test
    void 채팅방_나가기_성공() {
        // Arrange
        when(chatRoomRepository.findByRoomIdAndUserId(roomId, user1.getId())).thenReturn(
            Optional.of(chatRoom));
        // Act & Assert
        assertThatNoException().isThrownBy(() -> chatService.leaveChatRoom(roomId, user1.getId()));
        assertThat(chatRoom.getParticipantsStatus().get(user1.getId())).isFalse();
        assertThat(chatRoom.getChatStatus()).isEqualTo(ChatStatus.CLOSED);
    }

    @Test
    void 채팅방_나가기_실패() {
        // Arrange
        when(chatRoomRepository.findByRoomIdAndUserId(roomId, user1.getId())).thenReturn(
            Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> chatService.leaveChatRoom(roomId, USER1_ID))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅방_리스트_조회() {
        // Arrange
        var cursorId = 1L;
        var pageSize = 10;
        when(chatRoomRepository.findChatRoomsByUserId(USER1_ID, cursorId, pageSize)).thenReturn(
            List.of(chatRoom));
        // Act
        var actualResult = chatService.findChatRoomsByUserId(USER1_ID, cursorId, pageSize);
        // Assert
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(0))
            .hasFieldOrPropertyWithValue("cursorId", cursorId)
            .hasFieldOrPropertyWithValue("projectId", PROJECT_ID)
            .hasFieldOrPropertyWithValue("chatRoomId", roomId.toString())
            .hasFieldOrPropertyWithValue("status", ChatStatus.OPEN)
            .hasFieldOrProperty("ChatUserInfoRes")
            .hasFieldOrProperty("lastMessage")
            .hasFieldOrProperty("lastMessageCreatedAt");
        assertThat(actualResult.get(0).getChatUserInfoRes())
            .hasFieldOrPropertyWithValue("userId", USER2_ID)
            .hasFieldOrPropertyWithValue("username", "tidavid1")
            .hasFieldOrPropertyWithValue("avatarUrl", "https://avatars.githubusercontent.com/u/85854384?v=4");
    }

    @Test
    void 채팅방_세부_정보_조회_성공() {
        // Arrange
        when(chatRoomRepository.findByRoomIdAndUserId(roomId, USER1_ID)).thenReturn(
            Optional.of(chatRoom));
        // Act
        var actualResult = chatService.findChatRoomDetailData(roomId, USER1_ID);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("meetDetail", MEET_DETAIL)
            .hasFieldOrPropertyWithValue("meetStartTime", CURRENT_TIME)
            .hasFieldOrPropertyWithValue("meetLocationLatitude", MEET_LOCATION_LATITUDE)
            .hasFieldOrPropertyWithValue("meetLocationLongitude", MEET_LOCATION_LONGITUDE)
            .hasFieldOrPropertyWithValue("meetEndTime", CURRENT_TIME.plusHours(1))
            .hasFieldOrProperty("chatUserInfoRes");
        assertThat(actualResult.getChatUserInfoRes())
            .hasFieldOrPropertyWithValue("userId", USER2_ID)
            .hasFieldOrProperty("username")
            .hasFieldOrProperty("avatarUrl");
    }

    @Test
    void 채팅방_세부_정보_조회_실패_채팅방이_존재하지_않을_때() {
        // Arrange
        when(chatRoomRepository.findByRoomIdAndUserId(roomId, USER1_ID)).thenReturn(
            Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> chatService.findChatRoomDetailData(roomId, USER1_ID))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅방_ID_조회() {
        // Arrange
        when(chatRoomRepository.findByProjectId(PROJECT_ID)).thenReturn(Optional.of(chatRoom));
        // Act
        var actualResult = chatService.findChatRoomIdByProjectId(PROJECT_ID);
        // Assert
        assertThat(actualResult).isEqualTo(roomId);
    }

    @Test
    void 채팅_메시지_조회() {
        // Arrange
        var cursorId = 1L;
        var pageSize = 10;
        when(chatRoomRepository.findByRoomIdAndUserId(roomId, USER1_ID)).thenReturn(
            Optional.of(chatRoom));
        when(chatRepository.findAllByCollection(roomId.toString(), cursorId, pageSize)).thenReturn(
            List.of());
        // Act
        var actualResult = chatService.findChatMessagesByRoomId(roomId, USER1_ID, cursorId, pageSize);
        // Assert
        assertThat(actualResult).isEmpty();
    }
}