package io.oeid.mogakgo.domain.chat.entity.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.entity.vo.ChatRoomDetail;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.project.domain.entity.vo.MeetingInfo;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

@DisplayName("도큐먼트 테스트: ChatRoom")
class ChatRoomDocumentTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String MEET_DETAIL = "meetDetail";
    private static final Double MEET_LOCATION_LATITUDE = 37.579131361776746;
    private static final Double MEET_LOCATION_LONGITUDE = 126.97638456163786;
    private static final Long PROJECT_ID = 1L;
    private static final Long GITHUB_PK = 85854384L;
    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;
    private static final String USERNAME = "tidavid1";
    private static final String AVATAR_URL = "https://avatars.githubusercontent.com/u/85854384?v=4";
    private static final String GITHUB_URL = "https://github.com/tidavid1";
    private static final String REPOS_URL = "https://api.github.com/users/tidavid1/repos";

    private static User user1;
    private static User user2;
    private static ChatRoomDetail chatRoomDetail;

    private ChatRoom chatRoom;

    @BeforeAll
    static void setUp() {
        var project = mock(Project.class);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(project.getMeetingInfo()).thenReturn(
            MeetingInfo.of(CURRENT_TIME, CURRENT_TIME.plusHours(1),
                new GeometryFactory().createPoint(
                    new Coordinate(MEET_LOCATION_LATITUDE, MEET_LOCATION_LONGITUDE)), MEET_DETAIL));
        chatRoomDetail = ChatRoomDetail.from(project);
        user1 = spy(User.of(GITHUB_PK, USERNAME, AVATAR_URL, GITHUB_URL, REPOS_URL));
        user2 = spy(User.of(GITHUB_PK, USERNAME, AVATAR_URL, GITHUB_URL, REPOS_URL));
        doReturn(USER1_ID).when(user1).getId();
        doReturn(USER2_ID).when(user2).getId();
    }

    @BeforeEach
    void init() {
        chatRoom = ChatRoom.of(1L, UUID.randomUUID(), chatRoomDetail);
    }

    @Test
    void 채팅방_생성() {
        // Arrange
        var expectedCursorId = 1L;
        var expectedRoomId = UUID.randomUUID();
        // Act
        var actualResult = ChatRoom.of(expectedCursorId, expectedRoomId, chatRoomDetail);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("cursorId", expectedCursorId)
            .hasFieldOrPropertyWithValue("roomId", expectedRoomId)
            .hasFieldOrPropertyWithValue("chatStatus", ChatStatus.OPEN)
            .hasFieldOrProperty("chatRoomDetail").isNotNull()
            .hasFieldOrProperty("createdAt").isNotNull();
        assertThat(actualResult.getChatRoomDetail())
            .hasFieldOrPropertyWithValue("projectId", PROJECT_ID)
            .hasFieldOrPropertyWithValue("meetDetail", MEET_DETAIL)
            .hasFieldOrPropertyWithValue("meetLocationLatitude", MEET_LOCATION_LATITUDE)
            .hasFieldOrPropertyWithValue("meetLocationLongitude", MEET_LOCATION_LONGITUDE)
            .hasFieldOrPropertyWithValue("meetStartTime", CURRENT_TIME)
            .hasFieldOrPropertyWithValue("meetEndTime", CURRENT_TIME.plusHours(1));
    }

    @Test
    void 채팅방_유저_추가_성공() {
        // Act
        chatRoom.addParticipant(user1);
        // Assert
        assertThat(chatRoom.getParticipants()).hasSize(1);
        assertThat(chatRoom.getParticipants().get(USER1_ID))
            .hasFieldOrPropertyWithValue("userId", USER1_ID)
            .hasFieldOrPropertyWithValue("username", USERNAME)
            .hasFieldOrPropertyWithValue("avatarUrl", AVATAR_URL);
        assertThat(chatRoom.getParticipantsStatus()).hasSize(1);
        assertThat(chatRoom.getParticipantsStatus().get(USER1_ID)).isTrue();
    }

    @Test
    void 채팅방_유저_추가_실패_중복_유저() {
        // Arrange
        chatRoom.addParticipant(user1);
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.addParticipant(user1))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode400.CHAT_ROOM_USER_CANNOT_DUPLICATE.getMessage());
    }

    @Test
    void 채팅방_유저_추가_실패_최대_유저_초과() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
        User user3 = spy(User.of(GITHUB_PK, USERNAME, AVATAR_URL, GITHUB_URL, REPOS_URL));
        when(user3.getId()).thenReturn(3L);
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.addParticipant(user3))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode400.CHAT_ROOM_MAX_USER_SIZE.getMessage());
    }

    @Test
    void 채팅방_종료() {
        // Act
        chatRoom.closeChatRoom();
        // Assert
        assertThat(chatRoom.getChatStatus()).isEqualTo(ChatStatus.CLOSED);
    }

    @Test
    void 채팅방_유저_나가기_성공() {
        // Arrange
        chatRoom.addParticipant(user1);
        // Act
        chatRoom.leaveChatRoom(USER1_ID);
        // Assert
        assertThat(chatRoom.getParticipantsStatus().get(USER1_ID)).isFalse();
        assertThat(chatRoom.getChatStatus()).isEqualTo(ChatStatus.CLOSED);
    }

    @Test
    void 채팅방_유저_나가기_실패_유저_ID_NULL() {
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.leaveChatRoom(null))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_USER_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅방_유저_나가기_실패_이미_나간_채팅방() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.leaveChatRoom(USER1_ID);
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.leaveChatRoom(USER1_ID))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_USER_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅방_마지막_채팅_추가() {
        // Arrange
        var chatData = ChatMessage.builder()
            .id(1L)
            .message("test")
            .senderId(USER1_ID)
            .build();
        // Act
        chatRoom.updateLastMessage(chatData);
        // Assert
        assertThat(chatRoom.getLastMessage()).isEqualTo(chatData);
    }

    @Test
    void 채팅방_상대방_유저_조회_성공() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
        // Act
        var actualResult = chatRoom.getOpponentUserInfo(USER1_ID);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("userId", USER2_ID)
            .hasFieldOrPropertyWithValue("username", USERNAME)
            .hasFieldOrPropertyWithValue("avatarUrl", AVATAR_URL);
    }

    @Test
    void 채팅방_상대_유저_조회_실패_상대_유저가_존재하지_않을_때() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.getOpponentUserInfo(3L))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_USER_NOT_FOUND.getMessage());
    }

    @Test
    void 채팅방_유저_정보_조회_성공() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
        // Act
        var actualResult = chatRoom.getParticipantUserInfo(USER1_ID);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("userId", USER1_ID)
            .hasFieldOrPropertyWithValue("username", USERNAME)
            .hasFieldOrPropertyWithValue("avatarUrl", AVATAR_URL);
    }

    @Test
    void 채팅방_유저_정보_조회_실패_채팅방에_존재하지_않는_유저() {
        // Arrange
        chatRoom.addParticipant(user1);
        chatRoom.addParticipant(user2);
        // Act & Assert
        assertThatThrownBy(() -> chatRoom.getParticipantUserInfo(3L))
            .isInstanceOf(ChatException.class)
            .hasMessageContaining(ErrorCode404.CHAT_USER_NOT_FOUND.getMessage());
    }
}
