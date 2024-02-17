package io.oeid.mogakgo.domain.auth.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    private int refreshTokenExpirySeconds;

    public static JwtToken of(Long userId, String accessToken, String refreshToken,
        int refreshTokenExpirySeconds) {
        return new JwtToken(userId, accessToken, refreshToken, refreshTokenExpirySeconds);
    }

    public static JwtToken of(Long userId, String accessToken) {
        return new JwtToken(userId, accessToken, null, -1);
    }
}
