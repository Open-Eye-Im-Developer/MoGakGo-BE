package io.oeid.mogakgo.domain.chat.entity;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
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

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

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
    private ChatRoom(Project project, User creator, User sender) {
        validateUsers(creator, sender);
        this.project = project;
        this.creator = creator;
        this.sender = sender;
        this.status = ChatStatus.OPEN;
    }

    public void closeChat() {
        validateChatAvailableClosed();
        this.status = ChatStatus.CLOSED;
    }

    private void validateUsers(User creator, User sender) {
        if (creator.equals(sender)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_USER_CANNOT_DUPLICATE);
        }
    }

    public void validateContainsUser(User user) {
        if (!creator.equals(user) && !sender.equals(user)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_USER_NOT_CONTAINS);
        }
    }

    private void validateChatAvailableClosed() {
        if (this.status.equals(ChatStatus.CLOSED)) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_ALREADY_CLOSED);
        }
    }

}
