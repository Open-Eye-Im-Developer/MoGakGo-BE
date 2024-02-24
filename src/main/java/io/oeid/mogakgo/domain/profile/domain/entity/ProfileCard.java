package io.oeid.mogakgo.domain.profile.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode400.PROFILE_CARD_LIKE_AMOUNT_IS_ZERO;
import static io.oeid.mogakgo.exception.code.ErrorCode404.USER_NOT_FOUND;

import io.oeid.mogakgo.domain.profile.exception.ProfileCardException;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "profile_card_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column(name = "total_like_amount", nullable = false)
    private Long totalLikeAmount;

    @Builder
    private ProfileCard(User user) {
        validateUser(user);
        this.user = user;
        this.totalLikeAmount = 0L;
    }

    public void validateUser(User user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new UserException(USER_NOT_FOUND);
        }
    }

    public void increaseLikeAmount() {
        this.totalLikeAmount += 1;
    }

    public void decreaseLikeAmount() {
        if (this.totalLikeAmount <= 0) {
            throw new ProfileCardException(PROFILE_CARD_LIKE_AMOUNT_IS_ZERO);
        }
        this.totalLikeAmount -= 1;
    }

    public static ProfileCard from(User user) {
        return new ProfileCard(user);
    }
}
