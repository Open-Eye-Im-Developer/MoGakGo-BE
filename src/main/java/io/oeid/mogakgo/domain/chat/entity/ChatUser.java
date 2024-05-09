package io.oeid.mogakgo.domain.chat.entity;

import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Deprecated(forRemoval = true)
@Entity
@Getter
@Table(name = "chat_user_mapping_tb", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"chat_room_id", "user_id"}
    )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "available_yn")
    private Boolean availableYn;

    protected ChatUser(ChatRoom chatRoom, User user) {
        this.chatRoom = verifyChatRoom(chatRoom);
        this.user = verifyUser(user);
        this.availableYn = true;
        this.chatRoom.addChatUser(this);
    }

    public void disableChatUser() {
        availableYn = false;
    }

    private ChatRoom verifyChatRoom(ChatRoom chatRoom) {
        if (chatRoom == null) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_SHOULD_NOT_NULL);
        }
        return chatRoom;
    }

    private User verifyUser(User user) {
        if (user == null) {
            throw new ChatException(ErrorCode400.CHAT_USER_SHOULD_NOT_NULL);
        }
        return user;
    }
}
