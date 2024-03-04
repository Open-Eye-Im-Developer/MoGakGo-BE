package io.oeid.mogakgo.domain.chat.entity;

import io.oeid.mogakgo.domain.chat.entity.enums.ChatStatus;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter
@Table(name = "chat_room_tb", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"project_id"}
    )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    private static final int CHAT_USER_MAX_SIZE = 2;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChatStatus status;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ChatUser> chatUsers = new ArrayList<>();

    public ChatRoom(Project project) {
        this.project = project;
        this.status = ChatStatus.OPEN;
    }

    public ChatUser join(User user) {
        return new ChatUser(this, user);
    }

    public void closeChat() {
        if (this.status.equals(ChatStatus.OPEN)) {
            this.status = ChatStatus.CLOSED;
        }
    }

    public void leaveUser(User user) {
        ChatUser chatUser = chatUsers.stream()
            .filter(cu -> cu.getUser().equals(user))
            .findFirst()
            .orElseThrow(() -> new ChatException(ErrorCode404.CHAT_USER_NOT_FOUND));
        chatUser.disableChatUser();
    }

    protected void addChatUser(ChatUser chatUser) {
        if (chatUsers.size() >= CHAT_USER_MAX_SIZE) {
            throw new ChatException(ErrorCode400.CHAT_ROOM_MAX_USER_SIZE);
        }
        this.chatUsers.add(chatUser);
    }

}
