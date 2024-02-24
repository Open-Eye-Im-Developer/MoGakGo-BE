package io.oeid.mogakgo.domain.profile.application.dto.req;

import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "프로필 카드 생성 요청 DTO")
@Getter
public class UserProfileCardReq {

    @Schema(description = "프로필 카드를 생성할 사용자")
    @NotNull
    private final User user;

    private UserProfileCardReq(User user) {
        this.user = user;
    }

    public ProfileCard toEntity(User user) {
        return ProfileCard.from(user);
    }

    public static UserProfileCardReq of(User user) {
        return new UserProfileCardReq(user);
    }

}
