package io.oeid.mogakgo.domain.user.application.dto.res;

import io.oeid.mogakgo.domain.user.domain.User;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOAuth2Response {

    private final Long userId;
    private final Collection<GrantedAuthority> authorities;
    private final Boolean signUpComplete;

    public static UserOAuth2Response from(User user) {
        return new UserOAuth2Response(user.getId(), user.getAuthorities(), user.getSignupYn());
    }
}
