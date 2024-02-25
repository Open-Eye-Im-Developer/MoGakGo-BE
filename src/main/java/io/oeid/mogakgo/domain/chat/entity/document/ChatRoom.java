package io.oeid.mogakgo.domain.chat.entity.document;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "chat_room_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChatStatus status;

    @Builder
    private ChatRoom(String name, User creator, User sender) {
        this.name = name;
        validateUsers(creator, sender);
        this.creator = creator;
        this.sender = sender;
        this.status = ChatStatus.OPEN;
    }

    public void closeChat() {
        this.status = ChatStatus.CLOSED;
    }

    private void validateUsers(User creator, User sender) {
        if (creator.equals(sender)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_USER_CANNOT_DUPLICATE);
        }
    }

}
