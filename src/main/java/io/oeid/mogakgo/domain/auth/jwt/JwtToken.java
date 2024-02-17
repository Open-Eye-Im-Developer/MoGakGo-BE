package io.oeid.mogakgo.domain.auth.jwt;

import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    private Date refreshTokenExpiryDate;

    public static JwtToken of(Long userId, String accessToken, String refreshToken,
        Date refreshTokenExpiryDate) {
        return new JwtToken(userId, accessToken, refreshToken, refreshTokenExpiryDate);
    }

    public static JwtToken of(Long userId, String accessToken) {
        return new JwtToken(userId, accessToken, null, null);
    }
}
