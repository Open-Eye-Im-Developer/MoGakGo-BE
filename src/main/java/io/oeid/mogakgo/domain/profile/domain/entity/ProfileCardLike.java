package io.oeid.mogakgo.domain.profile.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode403.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.domain.profile.exception.ProfileCardLikeException;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "profile_card_like_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class ProfileCardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", updatable = false)
    private User receiver;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    private ProfileCardLike(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        validateSender(sender);
        validateReceiver(receiver);
    }

    private void validateSender(User tokenUser) {
        if (!this.sender.getId().equals(tokenUser.getId())) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_FORBIDDEN_OPERATION);
        }
    }

    private void validateReceiver(User receiver) {
        if (!this.receiver.getId().equals(receiver.getId())) {
            throw new UserException(USER_NOT_FOUND);
        }
    }

    public static ProfileCardLike of(User sender, User receiver) {
        return new ProfileCardLike(sender, receiver);
    }
}
